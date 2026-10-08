package com.shopwise.app.recommendation;

import com.shopwise.app.entity.*;
import com.shopwise.app.exception.NotFoundException;
import com.shopwise.app.repository.*;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecommendationService {
  public record Result(Long productId, String name, BigDecimal price, String source, double score) {}
  private final RecommendationDataProvider provider;
  private final RecommendationStrategy strategy;

  public RecommendationService(RecommendationDataProvider provider, RecommendationStrategy strategy) {
    this.provider = provider;
    this.strategy = strategy;
  }

  @Transactional(readOnly = true)
  public List<Result> recommend(Long productId, int limit) {
    List<Product> catalogue = provider.products().stream().sorted(Comparator.comparing(Product::getId)).toList();
    Map<Long, Integer> indices = new HashMap<>();
    for (int i = 0; i < catalogue.size(); i++) indices.put(catalogue.get(i).getId(), i);
    if (productId != null && !indices.containsKey(productId)) throw new NotFoundException("Product not found");
    if (catalogue.isEmpty()) return List.of();
    List<Set<Integer>> baskets = new ArrayList<>();
    double[] popularity = new double[catalogue.size()];
    Set<Integer> trainedProducts = new HashSet<>();
    for (Sale sale : provider.sales()) {
      Set<Integer> basket = new TreeSet<>();
      for (SaleItem item : sale.getSaleItems()) {
        if (item.getProduct() == null) continue;
        Integer index = indices.get(item.getProduct().getId());
        if (index != null && item.getQuantity() != null && item.getQuantity() > 0) {
          basket.add(index);
          popularity[index] += item.getQuantity();
        }
      }
      baskets.add(basket);
      if (basket.size() >= 2) trainedProducts.addAll(basket);
    }
    double[] scores = new double[catalogue.size()];
    boolean neural = productId != null && trainedProducts.contains(indices.get(productId));
    if (neural) {
      scores = strategy.score(catalogue.size(), baskets, indices.get(productId));
    }
    Set<Long> referenceCategories = productId == null ? Set.of() : categoryIds(catalogue.get(indices.get(productId)));
    List<Result> results = new ArrayList<>();
    for (int i = 0; i < catalogue.size(); i++) {
      Product product = catalogue.get(i);
      if (Objects.equals(product.getId(), productId)) continue;
      Set<Long> categories = categoryIds(product);
      Set<Long> union = new HashSet<>(categories); union.addAll(referenceCategories);
      categories.retainAll(referenceCategories);
      double similarity = union.isEmpty() ? 0 : (double) categories.size() / union.size();
      String source = neural && trainedProducts.contains(i) ? "NEURAL" : similarity > 0 ? "CATEGORY" : popularity[i] > 0 ? "POPULARITY" : "CATALOGUE";
      double score = source.equals("NEURAL") ? scores[i] : source.equals("CATEGORY") ? similarity : popularity[i];
      results.add(new Result(product.getId(), product.getName(), product.getPrice(), source, score));
    }
    return results.stream().sorted(Comparator.comparingInt((Result r) -> List.of("NEURAL", "CATEGORY", "POPULARITY", "CATALOGUE").indexOf(r.source())).thenComparing(Comparator.comparingDouble(Result::score).reversed()).thenComparing(Result::productId)).limit(limit).toList();
  }

  private Set<Long> categoryIds(Product product) {
    Set<Long> ids = new HashSet<>();
    for (Category category : product.getCategories()) ids.add(category.getId());
    return ids;
  }
}

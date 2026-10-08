package com.shopwise.app;

import com.shopwise.app.entity.*;
import com.shopwise.app.recommendation.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecommendationEvolutionTest {
  private Product product(long id) {
    Product p = new Product(); p.setId(id); p.setName("Product " + id); p.setPrice(BigDecimal.ONE); return p;
  }
  private Sale basket(Product... products) {
    Sale sale = new Sale();
    for (Product p : products) {
      SaleItem item = new SaleItem(); item.setProduct(p); item.setQuantity(1); sale.getSaleItems().add(item);
    }
    return sale;
  }
  @Test void replacesAlgorithmAndSourceAndReadsNewSales() {
    Product a = product(1), b = product(2), c = product(3);
    List<Sale> history = new ArrayList<>(); history.add(basket(a, b));
    var provider = new RecommendationDataProvider() {
      public List<Product> products() { return List.of(a, b, c); }
      public List<Sale> sales() { return history; }
    };
    var service = new RecommendationService(provider, (size, baskets, source) -> new double[] {0, .2, .9});
    assertEquals(2L, service.recommend(1L, 1).getFirst().productId());
    history.add(basket(a, c));
    var results = service.recommend(1L, 20);
    assertEquals(3L, results.getFirst().productId());
    assertEquals(2, results.size());
    assertEquals(results, service.recommend(1L, 20));
  }
  @Test void emptyHistoryUsesCategoriesAndEmptyCatalogueIsSafe() {
    Product a = product(1), b = product(2), c = product(3);
    Category category = new Category(); category.setId(1L);
    a.getCategories().add(category); c.getCategories().add(category);
    var provider = new RecommendationDataProvider() {
      public List<Product> products() { return List.of(a, b, c); }
      public List<Sale> sales() { return List.of(); }
    };
    var service = new RecommendationService(provider, (n, baskets, source) -> { throw new AssertionError(); });
    assertEquals(3L, service.recommend(1L, 5).getFirst().productId());
    assertEquals("CATEGORY", service.recommend(1L, 5).getFirst().source());
    var empty = new RecommendationDataProvider() {
      public List<Product> products() { return List.of(); }
      public List<Sale> sales() { return List.of(); }
    };
    assertTrue(new RecommendationService(empty, new NeuralRecommendationStrategy(1)).recommend(null, 5).isEmpty());
  }
  @Test void evaluatesReservedSyntheticBaskets() {
    List<Set<Integer>> training = new ArrayList<>();
    for (int repeat = 0; repeat < 8; repeat++) for (int i = 0; i < 8; i += 2) training.add(Set.of(i, i + 1));
    List<Set<Integer>> heldOut = List.of(Set.of(0, 1), Set.of(2, 3), Set.of(4, 5), Set.of(6, 7));
    NeuralModel model = new NeuralModel(8); model.train(training, 150);
    int neuralHits = 0, baselineHits = 0, cases = 0;
    for (Set<Integer> basket : heldOut) for (int target : new TreeSet<>(basket)) {
      int source = basket.stream().filter(i -> i != target).findFirst().orElseThrow();
      double[] x = new double[8]; x[source] = 1;
      double[] scores = model.predict(x);
      List<Integer> ranked = new ArrayList<>();
      for (int i = 0; i < 8; i++) if (i != source) ranked.add(i);
      var baseline = new ArrayList<>(ranked);
      ranked.sort(Comparator.comparingDouble((Integer i) -> scores[i]).reversed().thenComparingInt(i -> i));
      if (ranked.subList(0, 5).contains(target)) neuralHits++;
      if (baseline.subList(0, 5).contains(target)) baselineHits++;
      cases++;
    }
    System.out.printf("SYNTHETIC_HOLDOUT cases=%d neuralHitAt5=%.3f popularityHitAt5=%.3f%n", cases, (double) neuralHits / cases, (double) baselineHits / cases);
    assertEquals(8, cases); assertEquals(8, neuralHits); assertEquals(6, baselineHits);
  }
}

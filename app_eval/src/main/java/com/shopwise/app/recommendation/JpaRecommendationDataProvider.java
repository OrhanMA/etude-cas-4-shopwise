package com.shopwise.app.recommendation;

import com.shopwise.app.entity.*;
import com.shopwise.app.repository.*;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class JpaRecommendationDataProvider implements RecommendationDataProvider {
  private final ProductRepository products;
  private final SaleRepository sales;
  public JpaRecommendationDataProvider(ProductRepository products, SaleRepository sales) {
    this.products = products; this.sales = sales;
  }
  public List<Product> products() { return products.findAllForRecommendations(); }
  public List<Sale> sales() { return sales.findAllForRecommendations(); }
}

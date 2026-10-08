package com.shopwise.app.recommendation;

import com.shopwise.app.entity.Product;
import com.shopwise.app.entity.Sale;
import java.util.List;

/** Data-source boundary; JPA can be replaced without modifying the controller. */
public interface RecommendationDataProvider {
  List<Product> products();
  List<Sale> sales();
}

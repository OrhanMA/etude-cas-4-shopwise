package com.shopwise.app.repository;

import com.shopwise.app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  @org.springframework.data.jpa.repository.Query("select distinct p from Product p left join fetch p.categories")
  java.util.List<Product> findAllForRecommendations();
}

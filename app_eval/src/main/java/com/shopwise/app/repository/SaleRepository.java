package com.shopwise.app.repository;

import com.shopwise.app.entity.Sale;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
  @org.springframework.data.jpa.repository.Query("select distinct s from Sale s left join fetch s.saleItems i left join fetch i.product order by s.createdAt asc, s.id asc")
  List<Sale> findAllForRecommendations();
  List<Sale> findAllByOrderByCreatedAtDesc();
}

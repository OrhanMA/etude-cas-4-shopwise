package com.shopwise.app.repository;

import com.shopwise.app.entity.Sale;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
  List<Sale> findAllByOrderByCreatedAtDesc();
}

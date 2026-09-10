package com.shopwise.app.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SaleResponse {
  private Long id, userId;
  private BigDecimal totalPrice;
  private List<SaleItemResponse> items;
  private LocalDateTime createdAt, updatedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long v) {
    id = v;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long v) {
    userId = v;
  }

  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(BigDecimal v) {
    totalPrice = v;
  }

  public List<SaleItemResponse> getItems() {
    return items;
  }

  public void setItems(List<SaleItemResponse> v) {
    items = v;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime v) {
    createdAt = v;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime v) {
    updatedAt = v;
  }
}

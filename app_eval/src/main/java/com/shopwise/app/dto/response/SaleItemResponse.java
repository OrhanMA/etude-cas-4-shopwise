package com.shopwise.app.dto.response;

import java.math.BigDecimal;

public class SaleItemResponse {
  private Long id, productId;
  private Integer quantity;
  private BigDecimal unitPrice, lineTotal;

  public Long getId() {
    return id;
  }

  public void setId(Long v) {
    id = v;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long v) {
    productId = v;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer v) {
    quantity = v;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal v) {
    unitPrice = v;
  }

  public BigDecimal getLineTotal() {
    return lineTotal;
  }

  public void setLineTotal(BigDecimal v) {
    lineTotal = v;
  }
}

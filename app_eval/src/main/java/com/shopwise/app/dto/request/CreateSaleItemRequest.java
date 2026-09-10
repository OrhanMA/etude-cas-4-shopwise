package com.shopwise.app.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateSaleItemRequest {
  @NotNull private Long productId;
  @NotNull @Positive private Integer quantity;

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long value) {
    productId = value;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer value) {
    quantity = value;
  }
}

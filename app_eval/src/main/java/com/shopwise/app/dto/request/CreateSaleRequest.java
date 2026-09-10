package com.shopwise.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CreateSaleRequest {
  @NotNull private Long userId;
  @NotEmpty @Valid private List<CreateSaleItemRequest> items;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long v) {
    userId = v;
  }

  public List<CreateSaleItemRequest> getItems() {
    return items;
  }

  public void setItems(List<CreateSaleItemRequest> v) {
    items = v;
  }
}

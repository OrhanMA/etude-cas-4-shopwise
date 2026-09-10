package com.shopwise.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCategoryRequest {

  @NotBlank
  @Size(max = 255)
  private String name;

  public CreateCategoryRequest() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}

package com.shopwise.app.service;

import com.shopwise.app.dto.request.CreateCategoryRequest;
import com.shopwise.app.dto.request.UpdateCategoryRequest;
import com.shopwise.app.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {

  public CategoryResponse create(CreateCategoryRequest request);

  public CategoryResponse getById(Long id);

  public List<CategoryResponse> getAll();

  public CategoryResponse update(Long id, UpdateCategoryRequest request);

  public void delete(Long id);
}

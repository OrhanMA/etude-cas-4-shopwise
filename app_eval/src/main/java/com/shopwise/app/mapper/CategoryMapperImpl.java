package com.shopwise.app.mapper;

import com.shopwise.app.dto.request.*;
import com.shopwise.app.dto.response.CategoryResponse;
import com.shopwise.app.entity.Category;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapperImpl implements CategoryMapper {
  public Category toEntity(CreateCategoryRequest d) {
    Category e = new Category();
    e.setName(d.getName());
    return e;
  }

  public void updateEntity(UpdateCategoryRequest d, Category e) {
    e.setName(d.getName());
  }

  public CategoryResponse toResponse(Category e) {
    CategoryResponse d = new CategoryResponse();
    d.setId(e.getId());
    d.setName(e.getName());
    d.setCreatedAt(e.getCreatedAt());
    d.setUpdatedAt(e.getUpdatedAt());
    return d;
  }

  public List<CategoryResponse> toResponseList(List<Category> xs) {
    return xs.stream().map(this::toResponse).toList();
  }
}

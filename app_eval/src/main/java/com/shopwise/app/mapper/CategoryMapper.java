package com.shopwise.app.mapper;

import com.shopwise.app.dto.request.CreateCategoryRequest;
import com.shopwise.app.dto.request.UpdateCategoryRequest;
import com.shopwise.app.dto.response.CategoryResponse;
import com.shopwise.app.entity.Category;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  Category toEntity(CreateCategoryRequest dto);

  void updateEntity(UpdateCategoryRequest dto, @MappingTarget Category entity);

  CategoryResponse toResponse(Category category);

  List<CategoryResponse> toResponseList(List<Category> categories);
}

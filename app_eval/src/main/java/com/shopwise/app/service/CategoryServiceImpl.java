package com.shopwise.app.service;

import com.shopwise.app.dto.request.CreateCategoryRequest;
import com.shopwise.app.dto.request.UpdateCategoryRequest;
import com.shopwise.app.dto.response.CategoryResponse;
import com.shopwise.app.entity.Category;
import com.shopwise.app.exception.NotFoundException;
import com.shopwise.app.mapper.CategoryMapper;
import com.shopwise.app.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    super();
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  public CategoryResponse create(CreateCategoryRequest request) {
    Category category = categoryMapper.toEntity(request);
    Category saved = categoryRepository.save(category);
    return categoryMapper.toResponse(saved);
  }

  public CategoryResponse getById(Long id) {
    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Category not found"));
    return categoryMapper.toResponse(category);
  }

  public List<CategoryResponse> getAll() {
    List<Category> categories = categoryRepository.findAll();
    return categoryMapper.toResponseList(categories);
  }

  public CategoryResponse update(Long id, UpdateCategoryRequest request) {
    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Category not found"));

    categoryMapper.updateEntity(request, category);

    Category updated = categoryRepository.save(category);
    return categoryMapper.toResponse(updated);
  }

  public void delete(Long id) {
    if (!categoryRepository.existsById(id)) {
      throw new NotFoundException("Category not found");
    }
    categoryRepository.deleteById(id);
  }
}

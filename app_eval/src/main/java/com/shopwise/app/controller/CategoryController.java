package com.shopwise.app.controller;

import com.shopwise.app.dto.request.CreateCategoryRequest;
import com.shopwise.app.dto.request.UpdateCategoryRequest;
import com.shopwise.app.dto.response.CategoryResponse;
import com.shopwise.app.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @PostMapping
  public ResponseEntity<CategoryResponse> create(
      @Valid @RequestBody CreateCategoryRequest request) {
    CategoryResponse response = categoryService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
    CategoryResponse response = categoryService.getById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<CategoryResponse>> getAll() {
    List<CategoryResponse> categories = categoryService.getAll();
    return ResponseEntity.ok(categories);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryResponse> update(
      @PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
    CategoryResponse response = categoryService.update(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    categoryService.delete(id);
    return ResponseEntity.noContent().build();
  }
}

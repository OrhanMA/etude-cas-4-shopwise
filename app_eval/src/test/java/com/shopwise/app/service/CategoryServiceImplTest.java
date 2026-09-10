package com.shopwise.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.shopwise.app.dto.request.CreateCategoryRequest;
import com.shopwise.app.dto.response.CategoryResponse;
import com.shopwise.app.entity.Category;
import com.shopwise.app.mapper.CategoryMapper;
import com.shopwise.app.repository.CategoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CategoryServiceImplTest {
  @Mock CategoryRepository repository;
  @Mock CategoryMapper mapper;
  @InjectMocks CategoryServiceImpl service;

  @Test
  void create_maps_saves_and_returns_category() {
    CreateCategoryRequest request = new CreateCategoryRequest();
    Category entity = new Category();
    Category saved = new Category();
    CategoryResponse expected = new CategoryResponse();
    when(mapper.toEntity(request)).thenReturn(entity);
    when(repository.save(entity)).thenReturn(saved);
    when(mapper.toResponse(saved)).thenReturn(expected);
    assertThat(service.create(request)).isSameAs(expected);
    verify(repository).save(entity);
  }

  @Test
  void getById_throws_when_category_does_not_exist() {
    when(repository.findById(99L)).thenReturn(Optional.empty());
    org.junit.jupiter.api.Assertions.assertThrows(
        com.shopwise.app.exception.NotFoundException.class, () -> service.getById(99L));
  }
}

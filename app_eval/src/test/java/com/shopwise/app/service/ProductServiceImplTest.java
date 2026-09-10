package com.shopwise.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.shopwise.app.dto.request.CreateProductRequest;
import com.shopwise.app.dto.response.ProductResponse;
import com.shopwise.app.entity.Product;
import com.shopwise.app.mapper.ProductMapper;
import com.shopwise.app.repository.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ProductServiceImplTest {
  @Mock ProductRepository repository;
  @Mock ProductMapper mapper;
  @InjectMocks ProductServiceImpl service;

  @Test
  void create_delegates_to_mapper_and_repository() {
    CreateProductRequest request = new CreateProductRequest();
    Product entity = new Product();
    Product saved = new Product();
    ProductResponse response = new ProductResponse();
    when(mapper.toEntity(request)).thenReturn(entity);
    when(repository.save(entity)).thenReturn(saved);
    when(mapper.toResponse(saved)).thenReturn(response);
    assertThat(service.create(request)).isSameAs(response);
    verify(mapper).toEntity(request);
  }

  @Test
  void getAll_returns_mapped_products() {
    Product product = new Product();
    ProductResponse response = new ProductResponse();
    when(repository.findAll()).thenReturn(List.of(product));
    when(mapper.toResponseList(List.of(product))).thenReturn(List.of(response));
    assertThat(service.getAll()).containsExactly(response);
  }
}

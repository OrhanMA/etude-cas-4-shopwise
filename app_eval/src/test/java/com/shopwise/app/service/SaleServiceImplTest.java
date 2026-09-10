package com.shopwise.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.shopwise.app.dto.request.*;
import com.shopwise.app.dto.response.SaleResponse;
import com.shopwise.app.entity.*;
import com.shopwise.app.repository.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class SaleServiceImplTest {
  @Mock SaleRepository sales;
  @Mock UserRepository users;
  @Mock ProductRepository products;
  @InjectMocks SaleServiceImpl service;

  @Test
  void create_calculates_total_and_snapshots_product_prices() {
    User user = new User();
    user.setId(1L);
    Product coffee = new Product();
    coffee.setId(4L);
    coffee.setPrice(new BigDecimal("6.90"));
    Product water = new Product();
    water.setId(5L);
    water.setPrice(new BigDecimal("0.80"));
    CreateSaleItemRequest a = new CreateSaleItemRequest();
    a.setProductId(4L);
    a.setQuantity(2);
    CreateSaleItemRequest b = new CreateSaleItemRequest();
    b.setProductId(5L);
    b.setQuantity(1);
    CreateSaleRequest request = new CreateSaleRequest();
    request.setUserId(1L);
    request.setItems(List.of(a, b));
    when(users.findById(1L)).thenReturn(Optional.of(user));
    when(products.findById(4L)).thenReturn(Optional.of(coffee));
    when(products.findById(5L)).thenReturn(Optional.of(water));
    when(sales.save(any(Sale.class))).thenAnswer(i -> i.getArgument(0));
    SaleResponse response = service.create(request);
    assertThat(response.getTotalPrice()).isEqualByComparingTo("14.60");
    assertThat(response.getItems()).hasSize(2);
    assertThat(response.getItems())
        .extracting(com.shopwise.app.dto.response.SaleItemResponse::getUnitPrice)
        .containsExactlyInAnyOrder(new BigDecimal("6.90"), new BigDecimal("0.80"));
  }

  @Test
  void create_rejects_unknown_user() {
    CreateSaleRequest request = new CreateSaleRequest();
    request.setUserId(10L);
    request.setItems(List.of());
    when(users.findById(10L)).thenReturn(Optional.empty());
    org.junit.jupiter.api.Assertions.assertThrows(
        com.shopwise.app.exception.NotFoundException.class, () -> service.create(request));
    verifyNoInteractions(sales);
  }

  @Test
  void getAll_requests_sales_from_newest_to_oldest() {
    when(sales.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

    assertThat(service.getAll()).isEmpty();

    verify(sales).findAllByOrderByCreatedAtDesc();
    verify(sales, never()).findAll();
  }
}

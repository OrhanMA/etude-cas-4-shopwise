package com.shopwise.app.service;

import com.shopwise.app.dto.request.*;
import com.shopwise.app.dto.response.*;
import com.shopwise.app.entity.*;
import com.shopwise.app.exception.NotFoundException;
import com.shopwise.app.repository.*;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SaleServiceImpl implements SaleService {
  private final SaleRepository sales;
  private final UserRepository users;
  private final ProductRepository products;

  public SaleServiceImpl(SaleRepository sales, UserRepository users, ProductRepository products) {
    this.sales = sales;
    this.users = users;
    this.products = products;
  }

  public SaleResponse create(CreateSaleRequest request) {
    Sale sale = new Sale();
    apply(sale, request.getUserId(), request.getItems());
    return response(sales.save(sale));
  }

  public SaleResponse getById(Long id) {
    return response(sales.findById(id).orElseThrow(() -> new NotFoundException("Sale not found")));
  }

  public List<SaleResponse> getAll() {
    return sales.findAllByOrderByCreatedAtDesc().stream().map(this::response).toList();
  }

  public SaleResponse update(Long id, UpdateSaleRequest request) {
    Sale sale = sales.findById(id).orElseThrow(() -> new NotFoundException("Sale not found"));
    sale.getSaleItems().clear();
    apply(sale, request.getUserId(), request.getItems());
    return response(sales.save(sale));
  }

  public void delete(Long id) {
    if (!sales.existsById(id)) throw new NotFoundException("Sale not found");
    sales.deleteById(id);
  }

  private void apply(Sale sale, Long userId, List<CreateSaleItemRequest> requests) {
    sale.setUser(users.findById(userId).orElseThrow(() -> new NotFoundException("User not found")));
    BigDecimal total = BigDecimal.ZERO;
    for (CreateSaleItemRequest request : requests) {
      Product product =
          products
              .findById(request.getProductId())
              .orElseThrow(() -> new NotFoundException("Product not found"));
      SaleItem item = new SaleItem();
      item.setSale(sale);
      item.setProduct(product);
      item.setQuantity(request.getQuantity());
      item.setUnitPrice(product.getPrice());
      sale.getSaleItems().add(item);
      total = total.add(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
    }
    sale.setTotalPrice(total);
  }

  private SaleResponse response(Sale sale) {
    SaleResponse r = new SaleResponse();
    r.setId(sale.getId());
    r.setUserId(sale.getUser() == null ? null : sale.getUser().getId());
    r.setTotalPrice(sale.getTotalPrice());
    r.setCreatedAt(sale.getCreatedAt());
    r.setUpdatedAt(sale.getUpdatedAt());
    r.setItems(
        sale.getSaleItems().stream()
            .map(
                i -> {
                  SaleItemResponse x = new SaleItemResponse();
                  x.setId(i.getId());
                  x.setProductId(i.getProduct() == null ? null : i.getProduct().getId());
                  x.setQuantity(i.getQuantity());
                  x.setUnitPrice(i.getUnitPrice());
                  x.setLineTotal(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())));
                  return x;
                })
            .toList());
    return r;
  }
}

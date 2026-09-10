package com.shopwise.app.mapper;

import com.shopwise.app.dto.request.*;
import com.shopwise.app.dto.response.ProductResponse;
import com.shopwise.app.entity.Product;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProductMapperImpl implements ProductMapper {
  public Product toEntity(CreateProductRequest d) {
    Product e = new Product();
    e.setName(d.getName());
    e.setSku(d.getSku());
    e.setDescription(d.getDescription());
    e.setPrice(d.getPrice());
    return e;
  }

  public void updateEntity(UpdateProductRequest d, Product e) {
    e.setName(d.getName());
    e.setSku(d.getSku());
    e.setDescription(d.getDescription());
    e.setPrice(d.getPrice());
  }

  public ProductResponse toResponse(Product e) {
    ProductResponse d = new ProductResponse();
    d.setId(e.getId());
    d.setName(e.getName());
    d.setSku(e.getSku());
    d.setDescription(e.getDescription());
    d.setPrice(e.getPrice());
    d.setCreatedAt(e.getCreatedAt());
    d.setUpdatedAt(e.getUpdatedAt());
    return d;
  }

  public List<ProductResponse> toResponseList(List<Product> xs) {
    return xs.stream().map(this::toResponse).toList();
  }
}

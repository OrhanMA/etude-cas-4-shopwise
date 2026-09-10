package com.shopwise.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "SALE_ITEMS")
public class SaleItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "sale_id")
  private Sale sale;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  private Integer quantity;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal unitPrice;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Sale getSale() {
    return sale;
  }

  public void setSale(Sale sale) {
    this.sale = sale;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public SaleItem() {}

  public SaleItem(Long id, Sale sale, Product product, Integer quantity, BigDecimal unitPrice) {
    this.id = id;
    this.sale = sale;
    this.product = product;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
  }

  @Override
  public String toString() {
    return "SaleItem [id="
        + id
        + ", sale="
        + sale
        + ", product="
        + product
        + ", quantity="
        + quantity
        + ", unitPrice="
        + unitPrice
        + "]";
  }
}

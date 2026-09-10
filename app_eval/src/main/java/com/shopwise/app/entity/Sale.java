package com.shopwise.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "SALES")
public class Sale {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal totalPrice;

  @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<SaleItem> saleItems = new HashSet<>();

  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private User user;

  @CreationTimestamp private LocalDateTime createdAt;

  @UpdateTimestamp private LocalDateTime updatedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(BigDecimal totalPrice) {
    this.totalPrice = totalPrice;
  }

  public Set<SaleItem> getSaleItems() {
    return saleItems;
  }

  public void setSaleItems(Set<SaleItem> saleItems) {
    this.saleItems = saleItems;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Sale() {}

  public Sale(Long id, BigDecimal totalPrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.totalPrice = totalPrice;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String toString() {
    return "Sale [id="
        + id
        + ", totalPrice="
        + totalPrice
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + "]";
  }
}

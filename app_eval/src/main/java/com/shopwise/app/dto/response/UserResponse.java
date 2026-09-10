package com.shopwise.app.dto.response;

import java.time.LocalDateTime;

public class UserResponse {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String role;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long value) {
    id = value;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String value) {
    firstName = value;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String value) {
    lastName = value;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String value) {
    email = value;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String value) {
    role = value;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime value) {
    createdAt = value;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime value) {
    updatedAt = value;
  }
}

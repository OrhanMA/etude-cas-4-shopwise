package com.shopwise.app.mapper;

import com.shopwise.app.dto.request.*;
import com.shopwise.app.dto.response.UserResponse;
import com.shopwise.app.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {
  public User toEntity(CreateUserRequest d) {
    User e = new User();
    copy(d, e);
    return e;
  }

  public void updateEntity(UpdateUserRequest d, User e) {
    copy(d, e);
  }

  private void copy(CreateUserRequest d, User e) {
    e.setFirstName(d.getFirstName());
    e.setLastName(d.getLastName());
    e.setEmail(d.getEmail());
    e.setPasswordHash(d.getPassword());
    e.setRole(d.getRole());
  }

  public UserResponse toResponse(User e) {
    UserResponse d = new UserResponse();
    d.setId(e.getId());
    d.setFirstName(e.getFirstName());
    d.setLastName(e.getLastName());
    d.setEmail(e.getEmail());
    d.setRole(e.getRole());
    d.setCreatedAt(e.getCreatedAt());
    d.setUpdatedAt(e.getUpdatedAt());
    return d;
  }
}

package com.shopwise.app.service;

import com.shopwise.app.dto.request.CreateUserRequest;
import com.shopwise.app.dto.request.UpdateUserRequest;
import com.shopwise.app.dto.response.UserResponse;
import java.util.List;

public interface UserService {
  UserResponse create(CreateUserRequest request);

  UserResponse getById(Long id);

  List<UserResponse> getAll();

  UserResponse update(Long id, UpdateUserRequest request);

  void delete(Long id);
}

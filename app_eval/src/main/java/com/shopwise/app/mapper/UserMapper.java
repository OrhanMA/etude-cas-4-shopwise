package com.shopwise.app.mapper;

import com.shopwise.app.dto.request.CreateUserRequest;
import com.shopwise.app.dto.request.UpdateUserRequest;
import com.shopwise.app.dto.response.UserResponse;
import com.shopwise.app.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
  @Mapping(target = "passwordHash", source = "password")
  User toEntity(CreateUserRequest request);

  @Mapping(target = "passwordHash", source = "password")
  void updateEntity(UpdateUserRequest request, @MappingTarget User entity);

  UserResponse toResponse(User entity);
}

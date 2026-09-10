package com.shopwise.app.service;

import com.shopwise.app.dto.request.*;
import com.shopwise.app.dto.response.UserResponse;
import com.shopwise.app.entity.User;
import com.shopwise.app.exception.NotFoundException;
import com.shopwise.app.mapper.UserMapper;
import com.shopwise.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements UserService {
  private final UserRepository repository;
  private final UserMapper mapper;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(
      UserRepository repository, UserMapper mapper, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.mapper = mapper;
    this.passwordEncoder = passwordEncoder;
  }

  public UserResponse create(CreateUserRequest request) {
    User user = mapper.toEntity(request);
    user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    return mapper.toResponse(repository.save(user));
  }

  public UserResponse getById(Long id) {
    return mapper.toResponse(
        repository.findById(id).orElseThrow(() -> new NotFoundException("User not found")));
  }

  public List<UserResponse> getAll() {
    return repository.findAll().stream().map(mapper::toResponse).toList();
  }

  public UserResponse update(Long id, UpdateUserRequest request) {
    User entity =
        repository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    mapper.updateEntity(request, entity);
    entity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    return mapper.toResponse(repository.save(entity));
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) throw new NotFoundException("User not found");
    repository.deleteById(id);
  }
}

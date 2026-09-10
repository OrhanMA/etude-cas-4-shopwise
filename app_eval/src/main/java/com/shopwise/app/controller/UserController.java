package com.shopwise.app.controller;

import com.shopwise.app.dto.request.*;
import com.shopwise.app.dto.response.UserResponse;
import com.shopwise.app.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
  }

  @GetMapping("/{id}")
  public UserResponse getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @GetMapping
  public List<UserResponse> getAll() {
    return service.getAll();
  }

  @PutMapping("/{id}")
  public UserResponse update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest r) {
    return service.update(id, r);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}

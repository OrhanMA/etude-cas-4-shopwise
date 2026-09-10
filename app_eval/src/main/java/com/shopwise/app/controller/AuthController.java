package com.shopwise.app.controller;

import com.shopwise.app.dto.request.LoginRequest;
import com.shopwise.app.dto.response.LoginResponse;
import com.shopwise.app.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final long expirationSeconds;

  public AuthController(
      AuthenticationManager authenticationManager,
      JwtService jwtService,
      @Value("${app.security.jwt.expiration-seconds:3600}") long expirationSeconds) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.expirationSeconds = expirationSeconds;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    String token = jwtService.generateToken(authentication);
    return ResponseEntity.ok(new LoginResponse(token, "Bearer", expirationSeconds));
  }
}

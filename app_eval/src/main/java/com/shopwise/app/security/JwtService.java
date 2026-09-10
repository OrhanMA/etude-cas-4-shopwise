package com.shopwise.app.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final JwtEncoder jwtEncoder;
  private final long expirationSeconds;

  public JwtService(
      JwtEncoder jwtEncoder,
      @Value("${app.security.jwt.expiration-seconds:3600}") long expirationSeconds) {
    this.jwtEncoder = jwtEncoder;
    this.expirationSeconds = expirationSeconds;
  }

  public String generateToken(Authentication authentication) {
    Instant issuedAt = Instant.now();
    String role =
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(authority -> authority.startsWith("ROLE_"))
            .findFirst()
            .orElse("ROLE_USER");

    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .subject(authentication.getName())
            .issuedAt(issuedAt)
            .expiresAt(issuedAt.plus(expirationSeconds, ChronoUnit.SECONDS))
            .claim("role", role)
            .build();

    return jwtEncoder
        .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
        .getTokenValue();
  }
}

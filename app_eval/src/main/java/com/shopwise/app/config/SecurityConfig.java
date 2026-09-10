package com.shopwise.app.config;

import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecretKey jwtSecretKey(@Value("${app.security.jwt.secret}") String secret) {
    return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
  }

  @Bean
  JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
    return NimbusJwtDecoder.withSecretKey(jwtSecretKey).macAlgorithm(MacAlgorithm.HS256).build();
  }

  @Bean
  JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
    return NimbusJwtEncoder.withSecretKey(jwtSecretKey).build();
  }

  @Bean
  AuthenticationManager authenticationManager(
      UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return new ProviderManager(provider);
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers("/api/auth/login")
                    .permitAll()
                    // L'authentification de ces utilisateurs sera implementee dans l'US 5.
                    .requestMatchers(HttpMethod.POST, "/api/products/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/products/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/products/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/sales/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/sales/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/sales/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(
                        (request, response, exception) ->
                            writeJsonError(
                                response,
                                HttpServletResponse.SC_UNAUTHORIZED,
                                "Authentication required"))
                    .accessDeniedHandler(
                        (request, response, exception) ->
                            writeJsonError(
                                response,
                                HttpServletResponse.SC_FORBIDDEN,
                                "Access denied: ADMIN role required")));

    http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

    return http.build();
  }

  private static void writeJsonError(HttpServletResponse response, int status, String message)
      throws java.io.IOException {
    response.setStatus(status);
    response.setContentType("application/json");
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.getWriter().write("{\"status\":" + status + ",\"message\":\"" + message + "\"}");
  }
}

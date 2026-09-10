package com.shopwise.app.config;

import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorize ->
                authorize
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
                    // Les operations de lecture restent disponibles avant l'US 5.
                    .anyRequest()
                    .permitAll())
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(
                        (request, response, exception) ->
                            writeJsonError(
                                response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required"))
                    .accessDeniedHandler(
                        (request, response, exception) ->
                            writeJsonError(
                                response, HttpServletResponse.SC_FORBIDDEN,
                                "Access denied: ADMIN role required")));

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

package com.shopwise.app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private org.springframework.security.oauth2.jwt.JwtEncoder encoder;
  @Autowired private com.shopwise.app.repository.UserRepository users;

  @Test
  void expiredAndTamperedTokensAreRejected() throws Exception {
    var claims = org.springframework.security.oauth2.jwt.JwtClaimsSet.builder()
        .subject("marie.dupont@shopwise.test").claim("role", "ROLE_ADMIN")
        .claim("accountVersion", users.findByEmail("marie.dupont@shopwise.test").orElseThrow().getUpdatedAt().toString())
        .issuedAt(java.time.Instant.now().minusSeconds(7200))
        .expiresAt(java.time.Instant.now().minusSeconds(3600)).build();
    String expired = encoder.encode(org.springframework.security.oauth2.jwt.JwtEncoderParameters.from(
        org.springframework.security.oauth2.jwt.JwsHeader.with(
            org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256).build(), claims)).getTokenValue();
    for (String token : new String[]{expired, expired.substring(0, expired.lastIndexOf('.') + 1) + "invalid"}) {
      mockMvc.perform(get("/api/sales").header("Authorization", "Bearer " + token))
          .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401));
    }
  }

  @Test
  void validCredentialsReturnBearerToken() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginPayload()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isNotEmpty())
        .andExpect(jsonPath("$.tokenType").value("Bearer"));
  }

  @Test
  void invalidCredentialsReturnNormalized401() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"marie.dupont@shopwise.test\",\"password\":\"wrong\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.message").value("Invalid credentials"));
  }

  @Test
  void protectedEndpointWithoutTokenReturnsNormalized401() throws Exception {
    mockMvc
        .perform(get("/api/sales"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.message").value("Authentication required"));
  }

  @Test
  void invalidTokenReturnsNormalized401() throws Exception {
    mockMvc.perform(get("/api/sales").header("Authorization", "Bearer invalid"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.message").value("Authentication required"));
  }

  @Test
  void validTokenAllowsAccessToProtectedEndpoint() throws Exception {
    MvcResult login =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginPayload()))
            .andExpect(status().isOk())
            .andReturn();
    JsonNode body = objectMapper.readTree(login.getResponse().getContentAsString());

    mockMvc
        .perform(get("/api/sales").header("Authorization", "Bearer " + body.get("token").asText()))
        .andExpect(status().isOk());
  }

  private String loginPayload() {
    return "{\"email\":\"marie.dupont@shopwise.test\",\"password\":\"password\"}";
  }

  @Test
  void realAdminTokenAllowsWritesAndUserTokenDoesNot() throws Exception {
    for (String email : new String[] {"marie.dupont@shopwise.test", "lucas.martin@shopwise.test"}) {
      var login = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
          .content("{\"email\":\"" + email + "\",\"password\":\"password\"}"))
          .andExpect(status().isOk()).andReturn();
      String token = objectMapper.readTree(login.getResponse().getContentAsString()).get("token").asText();
      // Empty payload reaches validation for ADMIN, but must be refused for USER.
      mockMvc.perform(post("/api/sales").header("Authorization", "Bearer " + token)
          .contentType(MediaType.APPLICATION_JSON).content("{}"))
          .andExpect(status().is(email.startsWith("marie") ? 400 : 403));
    }
  }
}

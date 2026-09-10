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
}

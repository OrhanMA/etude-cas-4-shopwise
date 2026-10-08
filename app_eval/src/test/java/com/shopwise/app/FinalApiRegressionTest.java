package com.shopwise.app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@AutoConfigureMockMvc
class FinalApiRegressionTest {
  @Autowired MockMvc api;
  @Autowired WebApplicationContext context;
  @Autowired tools.jackson.databind.ObjectMapper json;

  void as(String email) throws Exception {
    api = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    var login = api.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
        .content("{\"email\":\"" + email + "@shopwise.test\",\"password\":\"password\"}"))
        .andExpect(status().isOk()).andReturn();
    String token = json.readTree(login.getResponse().getContentAsString()).get("token").asText();
    api = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity())
        .defaultRequest(get("/").header("Authorization", "Bearer " + token)).build();
  }

  @Test
  void anonymousCannotAccessAnyCrudOperation() throws Exception {
    api = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    for (String route : new String[]{"users", "categories", "products", "sales"}) {
      api.perform(get("/api/" + route)).andExpect(status().isUnauthorized());
      api.perform(get("/api/" + route + "/1")).andExpect(status().isUnauthorized());
      api.perform(post("/api/" + route).contentType(MediaType.APPLICATION_JSON).content("{}"))
          .andExpect(status().isUnauthorized());
      api.perform(put("/api/" + route + "/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
          .andExpect(status().isUnauthorized());
      api.perform(delete("/api/" + route + "/1")).andExpect(status().isUnauthorized());
    }
  }

  @Test
  void userCannotManageUsersOrWriteCategories() throws Exception {
    as("lucas.martin");
    api.perform(get("/api/users")).andExpect(status().isForbidden());
    api.perform(get("/api/users/2")).andExpect(status().isForbidden());
    for (String route : new String[]{"categories", "products", "sales"}) {
      api.perform(get("/api/" + route)).andExpect(status().isOk());
      api.perform(get("/api/" + route + "/1")).andExpect(status().isOk());
    }
    for (String route : new String[]{"users", "categories", "products", "sales"}) {
      api.perform(post("/api/" + route).contentType(MediaType.APPLICATION_JSON).content("{}"))
          .andExpect(status().isForbidden()).andExpect(jsonPath("$.status").value(403));
      api.perform(put("/api/" + route + "/2").contentType(MediaType.APPLICATION_JSON).content("{}"))
          .andExpect(status().isForbidden());
      api.perform(delete("/api/" + route + "/2")).andExpect(status().isForbidden());
    }
  }

  @Test
  void malformedJsonAndInvalidRoleAreRejected() throws Exception {
    as("marie.dupont");
    api.perform(post("/api/sales").contentType(MediaType.APPLICATION_JSON).content("{"))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    api.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
        .content("{\"firstName\":\"Test\",\"lastName\":\"Test\",\"email\":\"test@example.test\",\"password\":\"password\",\"role\":\"ROOT\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void duplicateAndReferencedResourceReturnConflict() throws Exception {
    as("marie.dupont");
    api.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
        .content("{\"sku\":\"CAFE-ARABICA-250\",\"name\":\"Duplicate\",\"price\":1}"))
        .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    api.perform(delete("/api/products/1")).andExpect(status().isConflict());
    api.perform(get("/api/products/1")).andExpect(status().isOk());
  }

  @Test
  void unknownRouteAndUnsupportedMethodAreNormalized() throws Exception {
    as("marie.dupont");
    api.perform(get("/api/does-not-exist")).andExpect(status().isNotFound());
    api.perform(patch("/api/products/1")).andExpect(status().isMethodNotAllowed());
  }
}

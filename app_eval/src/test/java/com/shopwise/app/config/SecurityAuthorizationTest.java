package com.shopwise.app.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.shopwise.app.controller.ProductController;
import com.shopwise.app.controller.SaleController;
import com.shopwise.app.dto.response.ProductResponse;
import com.shopwise.app.dto.response.SaleResponse;
import com.shopwise.app.service.ProductService;
import com.shopwise.app.service.SaleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(controllers = {ProductController.class, SaleController.class})
@Import(SecurityConfig.class)
class SecurityAuthorizationTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProductService productService;

  @MockitoBean private SaleService saleService;

  @BeforeEach
  void mockSuccessfulAdminResponses() {
    when(productService.create(any())).thenReturn(new ProductResponse());
    when(saleService.create(any())).thenReturn(new SaleResponse());
  }

  @Test
  void userCannotCreateProduct() throws Exception {
    mockMvc
        .perform(
            post("/api/products")
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(productPayload()))
        .andExpect(status().isForbidden())
        .andExpect(content().json("{\"status\":403,\"message\":\"Access denied: ADMIN role required\"}"));
  }

  @Test
  void userCannotCreateSale() throws Exception {
    mockMvc
        .perform(
            post("/api/sales")
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(salePayload()))
        .andExpect(status().isForbidden())
        .andExpect(content().json("{\"status\":403,\"message\":\"Access denied: ADMIN role required\"}"));
  }

  @Test
  void adminCanCreateProduct() throws Exception {
    mockMvc
        .perform(
            post("/api/products")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(productPayload()))
        .andExpect(status().isCreated());
  }

  private String productPayload() {
    return """
        {
          "name": "Produit securise",
          "sku": "SECURE-001",
          "price": 10.00
        }
        """;
  }

  private String salePayload() {
    return """
        {
          "userId": 1,
          "items": [{"productId": 1, "quantity": 1}]
        }
        """;
  }
}

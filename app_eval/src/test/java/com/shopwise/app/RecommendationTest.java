package com.shopwise.app;

import com.shopwise.app.recommendation.NeuralModel;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RecommendationTest {
  @Autowired MockMvc mvc;

  @Test void learnsMaskedProductAndIsReproducible() {
    var baskets = Collections.nCopies(12, Set.of(0, 1));
    var first = new NeuralModel(3);
    var second = new NeuralModel(3);
    double before = first.predict(new double[] {1, 0, 0})[1];
    assertEquals(24, first.train(baskets, 150));
    second.train(baskets, 150);
    double[] prediction = first.predict(new double[] {1, 0, 0});
    assertTrue(prediction[1] > before);
    assertTrue(prediction[1] > prediction[2]);
    assertArrayEquals(prediction, second.predict(new double[] {1, 0, 0}));
  }
  @Test void singleProductBasketsDoNotTrain() {
    assertEquals(0, new NeuralModel(2).train(List.of(Set.of(0)), 150));
  }
  @Test void endpointRequiresAuthentication() throws Exception {
    mvc.perform(get("/api/recommendations")).andExpect(status().isUnauthorized());
    mvc.perform(get("/api/recommendations").header("Authorization", "Bearer invalid")).andExpect(status().isUnauthorized());
  }
  @Test void authenticatedQueryUsesRealDatabase() throws Exception {
    mvc.perform(get("/api/recommendations").with(jwt()).param("limit", "1"))
      .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].productId").exists()).andExpect(jsonPath("$[0].source").exists());
  }
  @Test void rejectsInvalidAndUnknownParameters() throws Exception {
    mvc.perform(get("/api/recommendations").with(jwt()).param("limit", "0")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    mvc.perform(get("/api/recommendations").with(jwt()).param("productId", "999999")).andExpect(status().isNotFound());
  }
}

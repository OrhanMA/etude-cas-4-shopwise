package com.shopwise.app.recommendation;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.shopwise.app.exception.ApiError;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
  private final RecommendationService service;
  public RecommendationController(RecommendationService service) { this.service = service; }

  @GetMapping
  public ResponseEntity<?> get(@RequestParam(required = false) Long productId, @RequestParam(defaultValue = "5") int limit) {
    if (limit < 1 || limit > 20 || (productId != null && productId < 1)) return ResponseEntity.badRequest().body(new ApiError(400, "Invalid recommendation parameters"));
    return ResponseEntity.ok(service.recommend(productId, limit));
  }
}

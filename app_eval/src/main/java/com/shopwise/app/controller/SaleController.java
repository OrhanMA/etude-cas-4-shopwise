package com.shopwise.app.controller;

import com.shopwise.app.dto.request.*;
import com.shopwise.app.dto.response.SaleResponse;
import com.shopwise.app.service.SaleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
public class SaleController {
  private final SaleService service;

  public SaleController(SaleService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<SaleResponse> create(@Valid @RequestBody CreateSaleRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
  }

  @GetMapping("/{id}")
  public SaleResponse getById(@PathVariable Long id) {
    return service.getById(id);
  }

  @GetMapping
  public List<SaleResponse> getAll() {
    return service.getAll();
  }

  @PutMapping("/{id}")
  public SaleResponse update(@PathVariable Long id, @Valid @RequestBody UpdateSaleRequest r) {
    return service.update(id, r);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}

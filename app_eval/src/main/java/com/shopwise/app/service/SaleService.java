package com.shopwise.app.service;

import com.shopwise.app.dto.request.CreateSaleRequest;
import com.shopwise.app.dto.request.UpdateSaleRequest;
import com.shopwise.app.dto.response.SaleResponse;
import java.util.List;

public interface SaleService {
  SaleResponse create(CreateSaleRequest request);

  SaleResponse getById(Long id);

  List<SaleResponse> getAll();

  SaleResponse update(Long id, UpdateSaleRequest request);

  void delete(Long id);
}

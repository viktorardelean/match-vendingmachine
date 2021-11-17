package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.dto.ProductDto;
import javassist.tools.web.BadHttpRequest;

import java.util.List;

public interface ProductService {

  ProductDto saveProduct(ProductDto product) throws BadHttpRequest;

  ProductDto getProduct(Long productId) throws BadHttpRequest;

  List<ProductDto> getProducts();

  ProductDto updateProduct(Long productId, ProductDto productDto);

  void deleteProduct(Long productId);
}

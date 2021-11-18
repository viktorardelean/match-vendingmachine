package com.vardelean.vendingmachine.ut.service;

import com.vardelean.vendingmachine.dto.ProductDto;
import com.vardelean.vendingmachine.model.Product;

import java.util.List;

public interface ProductService {

  ProductDto saveProduct(ProductDto product);

  ProductDto getProduct(Long productId);

  Product getProductEntity(Long productId);

  List<ProductDto> getProducts();

  ProductDto updateProduct(Long productId, ProductDto productDto);

  void deleteProduct(Long productId);
}

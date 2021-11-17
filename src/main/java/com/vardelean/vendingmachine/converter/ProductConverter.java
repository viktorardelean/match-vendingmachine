package com.vardelean.vendingmachine.converter;

import com.vardelean.vendingmachine.dto.ProductDto;
import com.vardelean.vendingmachine.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductConverter {

  public Product toEntity(ProductDto productDto) {
    Product product = new Product();
    product.setProductName(productDto.getProductName());
    product.setCost(productDto.getCost());
    product.setAmountAvailable(productDto.getAmountAvailable());
    return product;
  }

  public Product toEntity(ProductDto productDto, Product product) {
    product.setProductName(productDto.getProductName());
    product.setCost(productDto.getCost());
    product.setAmountAvailable(productDto.getAmountAvailable());
    return product;
  }

  public ProductDto toDto(Product product) {
    ProductDto productDto = new ProductDto();
    productDto.setId(product.getId());
    productDto.setProductName(product.getProductName());
    productDto.setCost(product.getCost());
    productDto.setAmountAvailable(product.getAmountAvailable());
    productDto.setSellerId(product.getSeller().getId());
    return productDto;
  }
}

package com.vardelean.vendingmachine.api;

import com.vardelean.vendingmachine.dto.ProductDto;
import com.vardelean.vendingmachine.ut.service.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class ProductResourceIntegrationTest {

  private final ProductResource productResource;

  @Autowired
  public ProductResourceIntegrationTest(ProductServiceImpl productService) {
    productResource = new ProductResource(productService);
  }

  @Test
  void addProductSuccess() {
    final Long amountAvailable = 10L;
    final Long cost = 20L;
    final String productName = "snikers";
    final Long sellerId = 4L;
    ProductDto expectedProductDto = new ProductDto(amountAvailable, cost, productName, sellerId);
    ResponseEntity<ProductDto> addResponse = productResource.addProduct(expectedProductDto);

    assertEquals(amountAvailable, addResponse.getBody().getAmountAvailable());
    assertEquals(cost, addResponse.getBody().getCost());
    assertEquals(productName, addResponse.getBody().getProductName());
    assertEquals(sellerId, addResponse.getBody().getSellerId());

    ResponseEntity<ProductDto> getResponse =
        productResource.getProduct(addResponse.getBody().getId());

    assertEquals(addResponse.getBody(), getResponse.getBody());
  }

  @Test
  void addProductAmountNull() {
    final Long amountAvailable = 10L;
    final Long cost = 20L;
    final String productName = "snikers";
    final Long sellerId = 500L;
    ProductDto expectedProductDto = new ProductDto(amountAvailable, cost, productName, sellerId);
    assertThrows(
        ResponseStatusException.class, () -> productResource.addProduct(expectedProductDto));
  }
}

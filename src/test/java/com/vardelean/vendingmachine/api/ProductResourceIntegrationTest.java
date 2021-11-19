package com.vardelean.vendingmachine.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.vardelean.vendingmachine.dto.ProductDto;
import com.vardelean.vendingmachine.repo.ProductRepo;
import com.vardelean.vendingmachine.repo.VendingMachineUserRepo;
import com.vardelean.vendingmachine.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ProductResourceIntegrationTest {

  private static final String USERNAME = "user1";
  @Autowired private ProductResource productResource;
  @Autowired private VendingMachineUserRepo vendingMachineUserRepo;
  @Autowired private ProductRepo productRepo;

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

  @TestConfiguration
  public static class TestConfig {

    @Bean
    @Primary
    public JwtUtil mockSystemTypeDetector() {
      JwtUtil jwtUtil = mock(JwtUtil.class);
      final String jwt = "Bearer mockjwt";
      final DecodedJWT decodedJWTMock = mock(DecodedJWT.class);
      when(jwtUtil.extractToken(anyString())).thenReturn(Optional.of(jwt));
      when(jwtUtil.decodeToken(anyString())).thenReturn(decodedJWTMock);
      when(jwtUtil.extractUsername(decodedJWTMock)).thenReturn(USERNAME);
      return jwtUtil;
    }
  }
}

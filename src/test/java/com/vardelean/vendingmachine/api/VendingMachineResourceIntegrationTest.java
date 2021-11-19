package com.vardelean.vendingmachine.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.vardelean.vendingmachine.dto.*;
import com.vardelean.vendingmachine.model.Product;
import com.vardelean.vendingmachine.model.VendingMachineUser;
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

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class VendingMachineResourceIntegrationTest {

  private static final String USERNAME = "user1";
  @Autowired private VendingMachineResource vendingMachineResource;
  @Autowired private VendingMachineUserRepo vendingMachineUserRepo;
  @Autowired private ProductRepo productRepo;

  @Test
  void testDepositSuccess() {
    Optional<VendingMachineUser> vendingMachineUser =
        vendingMachineUserRepo.findByUsername(USERNAME);
    vendingMachineUser.get().setDeposit(0L);
    vendingMachineUserRepo.save(vendingMachineUser.get());

    final String authorizationHeader = "Bearer mockJwt";
    DepositRequestDto depositRequestDto =
        new DepositRequestDto(Map.of(5L, 1L, 10L, 2L, 20L, 3L, 50L, 4L, 100L, 5L));
    ResponseEntity<DepositResponseDto> depositResponse =
        vendingMachineResource.deposit(authorizationHeader, depositRequestDto);
    assertEquals(785L, depositResponse.getBody().getDepositAmount());

    Optional<VendingMachineUser> vendingMachineUserAfterDeposit =
        vendingMachineUserRepo.findByUsername(USERNAME);
    assertTrue(vendingMachineUserAfterDeposit.isPresent());
    assertEquals(785L, vendingMachineUserAfterDeposit.get().getDeposit());
  }

  @Test
  void testDepositEmptyCoins() {
    Optional<VendingMachineUser> vendingMachineUser =
        vendingMachineUserRepo.findByUsername(USERNAME);
    vendingMachineUser.get().setDeposit(0L);
    vendingMachineUserRepo.save(vendingMachineUser.get());

    final String authorizationHeader = "Bearer mockJwt";
    DepositRequestDto depositRequestDto = new DepositRequestDto(Map.of());
    ResponseEntity<DepositResponseDto> depositResponse =
        vendingMachineResource.deposit(authorizationHeader, depositRequestDto);
    assertEquals(0L, depositResponse.getBody().getDepositAmount());

    Optional<VendingMachineUser> vendingMachineUserAfterDeposit =
        vendingMachineUserRepo.findByUsername(USERNAME);
    assertTrue(vendingMachineUserAfterDeposit.isPresent());
    assertEquals(0L, vendingMachineUserAfterDeposit.get().getDeposit());
  }

  @Test
  void testDepositInvalidCoins() {
    Optional<VendingMachineUser> vendingMachineUser =
        vendingMachineUserRepo.findByUsername(USERNAME);
    vendingMachineUser.get().setDeposit(0L);
    vendingMachineUserRepo.save(vendingMachineUser.get());

    final String authorizationHeader = "Bearer mockJwt";
    DepositRequestDto depositRequestDto = new DepositRequestDto(Map.of(5L, 1L, 12L, 1L));
    assertThrows(
        ResponseStatusException.class,
        () -> vendingMachineResource.deposit(authorizationHeader, depositRequestDto));

    Optional<VendingMachineUser> vendingMachineUserAfterDeposit =
        vendingMachineUserRepo.findByUsername(USERNAME);
    assertTrue(vendingMachineUserAfterDeposit.isPresent());
    assertEquals(0L, vendingMachineUserAfterDeposit.get().getDeposit());
  }

  @Test
  void testBuySuccess() {
    final Long productId = 5L;
    final Long requestedAmount = 10L;

    Optional<VendingMachineUser> vendingMachineUser =
        vendingMachineUserRepo.findByUsername(USERNAME);
    vendingMachineUser.get().setDeposit(100L);
    vendingMachineUserRepo.save(vendingMachineUser.get());
    Optional<Product> product = productRepo.findById(productId);
    product.get().setAmountAvailable(10L);
    product.get().setCost(3L);
    productRepo.save(product.get());

    final String authorizationHeader = "Bearer mockJwt";
    ProductBuyDto productBuyDto = new ProductBuyDto(productId, requestedAmount);
    BuyRequestDto buyRequestDto = new BuyRequestDto(productBuyDto);
    ResponseEntity<BuyResponseDto> buyResponse =
        vendingMachineResource.buy(authorizationHeader, buyRequestDto);
    assertEquals(50L, buyResponse.getBody().getChange().get(0).getKey());
    assertEquals(1L, buyResponse.getBody().getChange().get(0).getValue());
    assertEquals(20L, buyResponse.getBody().getChange().get(1).getKey());
    assertEquals(1L, buyResponse.getBody().getChange().get(1).getValue());
    assertTrue(buyResponse.getBody().getProductsPurchased().isPresent());
    assertEquals(productBuyDto, buyResponse.getBody().getProductsPurchased().get());
    assertEquals(30L, buyResponse.getBody().getTotalSpent());

    Optional<VendingMachineUser> vendingMachineUserAfterBuy =
        vendingMachineUserRepo.findByUsername(USERNAME);
    assertTrue(vendingMachineUserAfterBuy.isPresent());
    assertEquals(0L, vendingMachineUserAfterBuy.get().getDeposit());

    Optional<Product> productAfterBuy = productRepo.findById(productId);
    assertTrue(productAfterBuy.isPresent());
    assertEquals(0L, productAfterBuy.get().getAmountAvailable());
  }

  @Test
  void testBuyInsufficientAmount() {
    final Long productId = 5L;
    final Long requestedAmount = 10L;

    Optional<VendingMachineUser> vendingMachineUser =
        vendingMachineUserRepo.findByUsername(USERNAME);
    vendingMachineUser.get().setDeposit(100L);
    vendingMachineUserRepo.save(vendingMachineUser.get());
    Optional<Product> product = productRepo.findById(productId);
    product.get().setAmountAvailable(5L);
    product.get().setCost(3L);
    productRepo.save(product.get());

    final String authorizationHeader = "Bearer mockJwt";
    ProductBuyDto productBuyDto = new ProductBuyDto(productId, requestedAmount);
    BuyRequestDto buyRequestDto = new BuyRequestDto(productBuyDto);
    assertThrows(
        ResponseStatusException.class,
        () -> vendingMachineResource.buy(authorizationHeader, buyRequestDto));

    Optional<VendingMachineUser> vendingMachineUserAfterBuy =
        vendingMachineUserRepo.findByUsername(USERNAME);
    assertTrue(vendingMachineUserAfterBuy.isPresent());
    assertEquals(100L, vendingMachineUserAfterBuy.get().getDeposit());

    Optional<Product> productAfterBuy = productRepo.findById(productId);
    assertTrue(productAfterBuy.isPresent());
    assertEquals(5L, productAfterBuy.get().getAmountAvailable());
  }

  @Test
  void testBuyInsufficientDeposit() {
    final Long productId = 5L;
    final Long requestedAmount = 100L;

    Optional<VendingMachineUser> vendingMachineUser =
        vendingMachineUserRepo.findByUsername(USERNAME);
    vendingMachineUser.get().setDeposit(100L);
    vendingMachineUserRepo.save(vendingMachineUser.get());
    Optional<Product> product = productRepo.findById(productId);
    product.get().setAmountAvailable(100L);
    product.get().setCost(3L);
    productRepo.save(product.get());

    final String authorizationHeader = "Bearer mockJwt";
    ProductBuyDto productBuyDto = new ProductBuyDto(productId, requestedAmount);
    BuyRequestDto buyRequestDto = new BuyRequestDto(productBuyDto);
    assertThrows(
        ResponseStatusException.class,
        () -> vendingMachineResource.buy(authorizationHeader, buyRequestDto));

    Optional<VendingMachineUser> vendingMachineUserAfterBuy =
        vendingMachineUserRepo.findByUsername(USERNAME);
    assertTrue(vendingMachineUserAfterBuy.isPresent());
    assertEquals(100L, vendingMachineUserAfterBuy.get().getDeposit());

    Optional<Product> productAfterBuy = productRepo.findById(productId);
    assertTrue(productAfterBuy.isPresent());
    assertEquals(100L, productAfterBuy.get().getAmountAvailable());
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

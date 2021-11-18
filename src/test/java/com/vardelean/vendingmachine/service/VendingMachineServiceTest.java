package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.dto.*;
import com.vardelean.vendingmachine.model.Product;
import com.vardelean.vendingmachine.model.VendingMachineUser;
import com.vardelean.vendingmachine.util.HttpUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VendingMachineServiceTest {

  private ProductService productService;
  private VendingMachineUserService vendingMachineUserService;
  private VendingMachineService vendingMachineService;
  private HttpUtil httpUtil;

  @BeforeEach
  void setupService() {
    productService = mock(ProductService.class);
    vendingMachineUserService = mock(VendingMachineUserService.class);
    httpUtil = new HttpUtil();
    vendingMachineService =
        new VendingMachineServiceImpl(vendingMachineUserService, productService, httpUtil);
  }

  @Test
  public void whenNewDeposit_thenShouldAddToExistingDeposit() {
    final String username = "user1";
    DepositRequestDto depositRequestDto =
        new DepositRequestDto(Map.of(5L, 1L, 10L, 2L, 20L, 3L, 50L, 1L, 100L, 1L));
    final Long existingDeposit = 100L;
    VendingMachineUser vendingMachineUser =
        new VendingMachineUser(
            1L,
            username,
            "pass1",
            existingDeposit,
            Collections.emptyList(),
            Collections.emptyList());
    when(vendingMachineUserService.getUserByUsername(username)).thenReturn(vendingMachineUser);
    DepositResponseDto depositResponseDto =
        vendingMachineService.deposit(username, depositRequestDto);

    assertEquals(235L + existingDeposit, depositResponseDto.getDepositAmount());
  }

  @Test
  public void whenDepositInvalidCoins_thenThrowException() {
    final String username = "user1";
    DepositRequestDto depositRequestDto = new DepositRequestDto(Map.of(7L, 1L));
    final Long existingDeposit = 100L;
    VendingMachineUser vendingMachineUser =
        new VendingMachineUser(
            1L,
            username,
            "pass1",
            existingDeposit,
            Collections.emptyList(),
            Collections.emptyList());
    when(vendingMachineUserService.getUserByUsername(username)).thenReturn(vendingMachineUser);
    assertThrows(
        ResponseStatusException.class,
        () -> vendingMachineService.deposit(username, depositRequestDto));
  }

  @Test
  public void whenNewDepositWithNegativeAmounts_thenNegativeAmountsShouldBeIgnored() {
    final String username = "user1";
    DepositRequestDto depositRequestDto =
        new DepositRequestDto(Map.of(5L, -1L, 10L, 2L, 20L, -3L, 50L, 0L, 100L, 1L));
    final Long existingDeposit = 100L;
    VendingMachineUser vendingMachineUser =
        new VendingMachineUser(
            1L,
            username,
            "pass1",
            existingDeposit,
            Collections.emptyList(),
            Collections.emptyList());
    when(vendingMachineUserService.getUserByUsername(username)).thenReturn(vendingMachineUser);
    DepositResponseDto depositResponseDto =
        vendingMachineService.deposit(username, depositRequestDto);

    assertEquals(120L + existingDeposit, depositResponseDto.getDepositAmount());
  }

  @Test
  public void whenBuyAll_thenChangeShouldBeEmpty() {
    final String username = "user1";
    final Long productId = 1L;
    final Long productAmount = 10L;
    final Long productCost = 10L;
    final Long productAvailableAmount = 10L;
    final Long existingDeposit = 100L;
    ProductBuyDto productBuyDto = new ProductBuyDto(1L, productAmount);
    BuyRequestDto buyRequestDto = new BuyRequestDto(productBuyDto);
    VendingMachineUser vendingMachineUser =
        new VendingMachineUser(
            1L,
            username,
            "pass1",
            existingDeposit,
            Collections.emptyList(),
            Collections.emptyList());
    ProductDto productDto =
        new ProductDto(
            productId, productAvailableAmount, productCost, "snickers", vendingMachineUser.getId());
    Product product = new Product(1L, productAvailableAmount, productCost, "snickers", null);
    when(vendingMachineUserService.getUserByUsername(username)).thenReturn(vendingMachineUser);
    when(productService.getProduct(productId)).thenReturn(productDto);
    when(productService.getProductEntity(productId)).thenReturn(product);
    BuyResponseDto buyResponseDto = vendingMachineService.buy(username, buyRequestDto);

    assertEquals(productAmount * productCost, buyResponseDto.getTotalSpent());
    assertEquals(productBuyDto, buyResponseDto.getProductsPurchased().orElse(null));
    assertEquals(Collections.emptyList(), buyResponseDto.getChange());
  }

  @Test
  public void whenBuyPartial_thenChangeShouldBeReturened() {
    final String username = "user1";
    final Long productId = 1L;
    final Long productAmount = 5L;
    final Long productCost = 5L;
    final Long productAvailableAmount = 10L;
    final Long existingDeposit = 1000L;
    ProductBuyDto productBuyDto = new ProductBuyDto(1L, productAmount);
    BuyRequestDto buyRequestDto = new BuyRequestDto(productBuyDto);
    VendingMachineUser vendingMachineUser =
        new VendingMachineUser(
            1L,
            username,
            "pass1",
            existingDeposit,
            Collections.emptyList(),
            Collections.emptyList());
    ProductDto productDto =
        new ProductDto(
            productId, productAvailableAmount, productCost, "snickers", vendingMachineUser.getId());
    Product product = new Product(1L, productAvailableAmount, productCost, "snickers", null);
    when(vendingMachineUserService.getUserByUsername(username)).thenReturn(vendingMachineUser);
    when(productService.getProduct(productId)).thenReturn(productDto);
    when(productService.getProductEntity(productId)).thenReturn(product);
    BuyResponseDto buyResponseDto = vendingMachineService.buy(username, buyRequestDto);

    assertEquals(productAmount * productCost, buyResponseDto.getTotalSpent());
    assertEquals(productBuyDto, buyResponseDto.getProductsPurchased().orElse(null));
    assertEquals(100l, buyResponseDto.getChange().get(0).getKey());
    assertEquals(9L, buyResponseDto.getChange().get(0).getValue());
    assertEquals(50L, buyResponseDto.getChange().get(1).getKey());
    assertEquals(1L, buyResponseDto.getChange().get(1).getValue());
    assertEquals(20l, buyResponseDto.getChange().get(2).getKey());
    assertEquals(1L, buyResponseDto.getChange().get(2).getValue());
    assertEquals(5l, buyResponseDto.getChange().get(3).getKey());
    assertEquals(1L, buyResponseDto.getChange().get(3).getValue());
  }

  @Test
  public void whenBuyOverDeposit_thenExceptionShouldBeThrown() {
    final String username = "user1";
    final Long productId = 1L;
    final Long productAmount = 21L;
    final Long productCost = 5L;
    final Long productAvailableAmount = 21L;
    final Long existingDeposit = 100L;
    ProductBuyDto productBuyDto = new ProductBuyDto(1L, productAmount);
    BuyRequestDto buyRequestDto = new BuyRequestDto(productBuyDto);
    VendingMachineUser vendingMachineUser =
        new VendingMachineUser(
            1L,
            username,
            "pass1",
            existingDeposit,
            Collections.emptyList(),
            Collections.emptyList());
    ProductDto productDto =
        new ProductDto(
            productId, productAvailableAmount, productCost, "snickers", vendingMachineUser.getId());
    Product product = new Product(1L, productAvailableAmount, productCost, "snickers", null);
    when(vendingMachineUserService.getUserByUsername(username)).thenReturn(vendingMachineUser);
    when(productService.getProduct(productId)).thenReturn(productDto);
    when(productService.getProductEntity(productId)).thenReturn(product);
    assertThrows(
        ResponseStatusException.class, () -> vendingMachineService.buy(username, buyRequestDto));
  }

  @Test
  public void whenBuyOverAmount_thenExceptionShouldBeThrown() {
    final String username = "user1";
    final Long productId = 1L;
    final Long productAmount = 20L;
    final Long productCost = 5L;
    final Long productAvailableAmount = 19L;
    final Long existingDeposit = 100L;
    ProductBuyDto productBuyDto = new ProductBuyDto(1L, productAmount);
    BuyRequestDto buyRequestDto = new BuyRequestDto(productBuyDto);
    VendingMachineUser vendingMachineUser =
        new VendingMachineUser(
            1L,
            username,
            "pass1",
            existingDeposit,
            Collections.emptyList(),
            Collections.emptyList());
    ProductDto productDto =
        new ProductDto(
            productId, productAvailableAmount, productCost, "snickers", vendingMachineUser.getId());
    Product product = new Product(1L, productAvailableAmount, productCost, "snickers", null);
    when(vendingMachineUserService.getUserByUsername(username)).thenReturn(vendingMachineUser);
    when(productService.getProduct(productId)).thenReturn(productDto);
    when(productService.getProductEntity(productId)).thenReturn(product);
    assertThrows(
        ResponseStatusException.class, () -> vendingMachineService.buy(username, buyRequestDto));
  }
}

package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.dto.*;
import com.vardelean.vendingmachine.model.Product;
import com.vardelean.vendingmachine.model.VendingMachineUser;
import com.vardelean.vendingmachine.repo.ProductRepo;
import com.vardelean.vendingmachine.repo.VendingMachineUserRepo;
import com.vardelean.vendingmachine.util.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class VendingMachineServiceImpl implements VendingMachineService {

  private static final Set<Long> VALID_COINS =
      new HashSet<>(Arrays.asList(5L, 10L, 20L, 50L, 100L));
  private final VendingMachineUserRepo vendingMachineUserRepo;
  private final ProductRepo productRepo;
  private final HttpUtil httpUtil;

  @Override
  public DepositResponseDto deposit(
      @NonNull String username, @NonNull DepositRequestDto depositRequestDto) {
    log.info("Deposit coins {} for username : {}", depositRequestDto.getCoins(), username);
    Optional<VendingMachineUser> vendingMachineUser =
        vendingMachineUserRepo.findByUsername(username);
    return vendingMachineUser
        .map(
            user -> {
              final Long currentDeposit = user.getDeposit();
              final Long depositAmount = calculateTotalAmount(depositRequestDto.getCoins());
              final Long newDeposit = currentDeposit + depositAmount;
              user.setDeposit(newDeposit);
              return new DepositResponseDto(newDeposit);
            })
        .orElseThrow(() -> httpUtil.badRequest(username, "User does not exists: "));
  }

  @Override
  public BuyResponseDto buy(@NonNull String username, @NonNull BuyRequestDto buyRequestDto) {
    log.info("Buy poducts {} as user : {}", buyRequestDto.getProducts(), username);
    Optional<VendingMachineUser> vendingMachineUser =
        vendingMachineUserRepo.findByUsername(username);
    return vendingMachineUser
        .map(
            user -> {
              final Long deposit = user.getDeposit();
              if (isDepositSufficient(deposit, buyRequestDto.getProducts())) {

              } else {
                throw httpUtil.badRequest(
                    deposit, "User deposit amount is not sufficient for purchase");
              }
              return new BuyResponseDto();
            })
        .orElseThrow(() -> httpUtil.badRequest(username, "User does not exists: "));
  }

  private Long calculateTotalAmount(final Map<Long, Long> coins) {
    if (coins == null) {
      return 0L;
    }
    if (valid(coins)) {
      return coins.entrySet().stream()
          .reduce(
              0L,
              (partialSum, coinEntry) -> partialSum + coinEntry.getKey() * coinEntry.getValue(),
              Long::sum);
    } else {
      throw httpUtil.badRequest(coins, "Invalid coins inserted: ");
    }
  }

  private boolean valid(Map<Long, Long> coins) {
    return VALID_COINS.containsAll(coins.keySet());
  }

  private boolean isDepositSufficient(Long deposit, List<ProductBuyDto> products) {
    return deposit >= totalAmount(products);
  }

  private Long totalAmount(List<ProductBuyDto> products) {
    return products.stream()
        .reduce(
            0L,
            (partialSum, product) ->
                partialSum + getCost(product.getProductId()) * product.getAmount(),
            Long::sum);
  }

  private Long getCost(Long productId) {
    Optional<Product> productOptional = productRepo.findById(productId);
    return productOptional
        .map(Product::getCost)
        .orElseThrow(() -> httpUtil.badRequest(productId, "Invalid product id: "));
  }
}

package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.dto.*;
import com.vardelean.vendingmachine.model.Product;
import com.vardelean.vendingmachine.model.VendingMachineUser;
import com.vardelean.vendingmachine.util.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.vardelean.vendingmachine.util.ErrorMessages.*;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class VendingMachineServiceImpl implements VendingMachineService {

  private static final List<Long> VALID_COINS = Arrays.asList(5L, 10L, 20L, 50L, 100L);
  private final VendingMachineUserService vendingMachineUserService;
  private final ProductService productService;
  private final HttpUtil httpUtil;

  @Override
  public DepositResponseDto deposit(
      @NonNull String username, @NonNull DepositRequestDto depositRequestDto) {
    log.info("Deposit coins {} for username : {}", depositRequestDto.getCoins(), username);
    VendingMachineUser vendingMachineUser = vendingMachineUserService.getUserByUsername(username);
    final Long currentDeposit = vendingMachineUser.getDeposit();
    final Long depositAmount = calculateTotalAmount(depositRequestDto.getCoins());
    final Long newDeposit = currentDeposit + depositAmount;
    vendingMachineUser.setDeposit(newDeposit);
    return new DepositResponseDto(newDeposit);
  }

  @Override
  public BuyResponseDto buy(@NonNull String username, @NonNull BuyRequestDto buyRequestDto) {
    log.info("Buy products {} as user : {}", buyRequestDto.getProduct(), username);
    VendingMachineUser vendingMachineUser = vendingMachineUserService.getUserByUsername(username);
    final Long deposit = vendingMachineUser.getDeposit();
    final AbstractMap.SimpleImmutableEntry<ProductDto, Long> productWithRequestedAmount =
        getProductWithRequestedAmount(buyRequestDto.getProduct());
    final Long totalRequestedCost = getTotalCost(productWithRequestedAmount);
    if (isDepositSufficient(deposit, totalRequestedCost)) {
      AbstractMap.SimpleImmutableEntry<Long, List<AbstractMap.SimpleImmutableEntry<Long, Long>>>
          change = getChange(deposit, totalRequestedCost);
      vendingMachineUser.setDeposit(change.getKey());
      substractProductAmount(buyRequestDto.getProduct());
      return new BuyResponseDto(
          totalRequestedCost, getPurchasedProduct(buyRequestDto.getProduct()), change.getValue());
    } else {
      throw httpUtil.badRequest(ERROR_INSUFFICIENT_DEPOSIT, deposit);
    }
  }

  private void substractProductAmount(ProductBuyDto productBuyDto) {
    Product purchasedProduct = productService.getProductEntity(productBuyDto.getProductId());
    Long availableAmount = purchasedProduct.getAmountAvailable();
    purchasedProduct.setAmountAvailable(availableAmount - productBuyDto.getRequestedAmount());
  }

  @NonNull
  private Long calculateTotalAmount(@NonNull final Map<Long, Long> coins) {
    if (valid(coins)) {
      return coins.entrySet().stream()
          .filter(coin -> coin.getValue() >= 0)
          .reduce(
              0L,
              (partialSum, coinEntry) -> partialSum + coinEntry.getKey() * coinEntry.getValue(),
              Long::sum);
    } else {
      throw httpUtil.badRequest(ERROR_INVALID_COINS, coins);
    }
  }

  private boolean valid(@NonNull final Map<Long, Long> coins) {
    return VALID_COINS.containsAll(coins.keySet());
  }

  private boolean isDepositSufficient(
      @NonNull final Long deposit, @NonNull final Long totalAmount) {
    return deposit >= totalAmount;
  }

  @NonNull
  private AbstractMap.SimpleImmutableEntry<ProductDto, Long> getProductWithRequestedAmount(
      @NonNull final ProductBuyDto productBuyDto) {
    ProductDto productDto = productService.getProduct(productBuyDto.getProductId());
    if (productBuyDto.getRequestedAmount() > productDto.getAmountAvailable()) {
      throw httpUtil.badRequest(ERROR_INSUFFICIENT_AMOUNT, productDto.getAmountAvailable());
    }

    return new AbstractMap.SimpleImmutableEntry<>(productDto, productBuyDto.getRequestedAmount());
  }

  @NonNull
  private Long getTotalCost(
      @NonNull final AbstractMap.SimpleImmutableEntry<ProductDto, Long> productAmount) {
    if (productAmount.getValue() > 0) {
      return productAmount.getKey().getCost() * productAmount.getValue();
    } else {
      return 0L;
    }
  }

  private Optional<ProductBuyDto> getPurchasedProduct(@NonNull final ProductBuyDto productBuyDto) {
    if (productBuyDto.getRequestedAmount() > 0) {
      return Optional.of(productBuyDto);
    } else {
      return Optional.empty();
    }
  }

  @NonNull
  private AbstractMap.SimpleImmutableEntry<Long, List<AbstractMap.SimpleImmutableEntry<Long, Long>>>
      getChange(@NonNull final Long deposit, @NonNull final Long totalAmount) {

    List<AbstractMap.SimpleImmutableEntry<Long, Long>> changeList = new ArrayList<>();
    Long changeAmount = deposit - totalAmount;
    VALID_COINS.sort(Comparator.reverseOrder());
    for (Long coin : VALID_COINS) {
      Long coinAmount = getCoinAmount(coin, changeAmount);
      if (coinAmount > 0L) {
        changeList.add(new AbstractMap.SimpleImmutableEntry<>(coin, coinAmount));
        changeAmount %= coin;
      }
    }
    return new AbstractMap.SimpleImmutableEntry<>(changeAmount, changeList);
  }

  @NonNull
  private Long getCoinAmount(Long coin, Long changeAmount) {
    if (coin <= 0) {
      return 0L;
    }
    return changeAmount / coin;
  }
}

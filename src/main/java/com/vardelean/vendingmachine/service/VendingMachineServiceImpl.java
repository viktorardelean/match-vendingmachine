package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.dto.BuyRequestDto;
import com.vardelean.vendingmachine.dto.BuyResponseDto;
import com.vardelean.vendingmachine.dto.DepositRequestDto;
import com.vardelean.vendingmachine.dto.DepositResponseDto;
import com.vardelean.vendingmachine.model.VendingMachineUser;
import com.vardelean.vendingmachine.repo.VendingMachineUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class VendingMachineServiceImpl implements VendingMachineService {

  private static final Set<Long> VALID_COINS =
      new HashSet<>(Arrays.asList(5L, 10L, 20L, 50L, 100L));
  private final VendingMachineUserRepo vendingMachineUserRepo;

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
        .orElseThrow(
            () -> {
              log.error("User not found in the DB: {}", username);
              throw new ResponseStatusException(
                  HttpStatus.BAD_REQUEST, "User does not exists: " + username);
            });
  }

  @Override
  public BuyResponseDto buy(@NonNull String username, @NonNull BuyRequestDto buyRequestDto) {
    return null;
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
      log.error("Invalid coins inserted: {}", coins);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid coins inserted: " + coins);
    }
  }

  private boolean valid(Map<Long, Long> coins) {
    return VALID_COINS.containsAll(coins.keySet());
  }
}

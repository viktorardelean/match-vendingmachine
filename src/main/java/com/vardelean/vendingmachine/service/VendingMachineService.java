package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.dto.BuyRequestDto;
import com.vardelean.vendingmachine.dto.BuyResponseDto;
import com.vardelean.vendingmachine.dto.DepositRequestDto;
import com.vardelean.vendingmachine.dto.DepositResponseDto;

public interface VendingMachineService {

  DepositResponseDto deposit(String username, DepositRequestDto depositRequestDto);

  BuyResponseDto buy(String username, BuyRequestDto buyRequestDto);
}

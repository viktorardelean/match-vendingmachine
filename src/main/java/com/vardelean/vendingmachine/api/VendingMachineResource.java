package com.vardelean.vendingmachine.api;

import com.vardelean.vendingmachine.dto.BuyRequestDto;
import com.vardelean.vendingmachine.dto.BuyResponseDto;
import com.vardelean.vendingmachine.dto.DepositRequestDto;
import com.vardelean.vendingmachine.dto.DepositResponseDto;
import com.vardelean.vendingmachine.service.VendingMachineService;
import com.vardelean.vendingmachine.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VendingMachineResource {
  private final VendingMachineService vendingMachineService;
  private final JwtUtil jwtUtil;

  @PatchMapping("/deposit")
  public ResponseEntity<DepositResponseDto> deposit(
      @RequestHeader(name = "Authorization") String authorizationHeader,
      @RequestBody DepositRequestDto depositRequestDto) {
    Optional<String> jwt = jwtUtil.extractToken(authorizationHeader);
    String username = jwtUtil.extractUsername(jwtUtil.decodeToken(jwt.get()));
    return ResponseEntity.ok().body(vendingMachineService.deposit(username, depositRequestDto));
  }

  @PutMapping("/buy")
  public ResponseEntity<BuyResponseDto> buy(
      @RequestHeader(name = "Authorization") String authorizationHeader,
      @RequestBody BuyRequestDto buyRequestDto) {
    Optional<String> jwt = jwtUtil.extractToken(authorizationHeader);
    String username = jwtUtil.extractUsername(jwtUtil.decodeToken(jwt.get()));
    return ResponseEntity.ok().body(vendingMachineService.buy(username, buyRequestDto));
  }
}

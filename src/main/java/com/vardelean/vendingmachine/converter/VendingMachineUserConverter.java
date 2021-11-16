package com.vardelean.vendingmachine.converter;

import com.vardelean.vendingmachine.dto.VendingMachineUserDto;
import com.vardelean.vendingmachine.model.VendingMachineUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class VendingMachineUserConverter {

  private final PasswordEncoder passwordEncoder;

  public VendingMachineUser toEntity(VendingMachineUserDto vendingMachineUserDto) {
    VendingMachineUser vendingMachineUser = new VendingMachineUser();
    vendingMachineUser.setUsername(vendingMachineUserDto.getUsername());
    vendingMachineUser.setDeposit(vendingMachineUserDto.getDeposit());
    vendingMachineUser.setPassword(passwordEncoder.encode(vendingMachineUserDto.getPassword()));
    return vendingMachineUser;
  }
}

package com.vardelean.vendingmachine.converter;

import com.vardelean.vendingmachine.dto.VendingMachineUserDto;
import com.vardelean.vendingmachine.model.VendingMachineUser;
import org.springframework.stereotype.Component;

@Component
public class VendingMachineUserConverter {

  public VendingMachineUser toEntity(VendingMachineUserDto vendingMachineUserDto) {
    VendingMachineUser vendingMachineUser = new VendingMachineUser();
    vendingMachineUser.setUsername(vendingMachineUserDto.getUsername());
    vendingMachineUser.setDeposit(vendingMachineUserDto.getDeposit());
    vendingMachineUser.setPassword(vendingMachineUserDto.getPassword());
    return vendingMachineUser;
  }
}

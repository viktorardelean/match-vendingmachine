package com.vardelean.vendingmachine.converter;

import com.vardelean.vendingmachine.dto.VendingMachineUserDto;
import com.vardelean.vendingmachine.model.Role;
import com.vardelean.vendingmachine.model.VendingMachineUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

  public VendingMachineUser toEntity(
      VendingMachineUserDto vendingMachineUserDto, VendingMachineUser vendingMachineUser) {
    vendingMachineUser.setUsername(vendingMachineUserDto.getUsername());
    vendingMachineUser.setDeposit(vendingMachineUserDto.getDeposit());
    vendingMachineUser.setPassword(passwordEncoder.encode(vendingMachineUserDto.getPassword()));
    return vendingMachineUser;
  }

  public VendingMachineUserDto toDto(VendingMachineUser vendingMachineUser) {
    VendingMachineUserDto vendingMachineUserDto = new VendingMachineUserDto();
    vendingMachineUserDto.setId(vendingMachineUser.getId());
    vendingMachineUserDto.setUsername(vendingMachineUser.getUsername());
    vendingMachineUserDto.setDeposit(vendingMachineUser.getDeposit());
    Optional<Role> roleOptional = vendingMachineUser.getRoles().stream().findFirst();
    roleOptional.ifPresent(role -> vendingMachineUserDto.setRoleName(role.getName()));
    return vendingMachineUserDto;
  }
}

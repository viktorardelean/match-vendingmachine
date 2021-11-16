package com.vardelean.vendingmachine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendingMachineUserDto {

  private String username;
  private String password;
  private Long deposit;
  private String roleName;
}

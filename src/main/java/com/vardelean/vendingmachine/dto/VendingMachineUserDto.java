package com.vardelean.vendingmachine.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class VendingMachineUserDto {

  private Long id;
  @NonNull private String username;
  @NonNull private String password;
  @NonNull private Long deposit;
  @NonNull private String roleName;
}

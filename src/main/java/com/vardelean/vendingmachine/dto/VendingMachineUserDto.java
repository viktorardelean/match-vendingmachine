package com.vardelean.vendingmachine.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class VendingMachineUserDto {

  private Long id;
  @NonNull @NotEmpty private String username;

  @NonNull @NotEmpty private String password;

  @NonNull @NotNull @PositiveOrZero private Long deposit;

  @NonNull @NotEmpty private String roleName;
}

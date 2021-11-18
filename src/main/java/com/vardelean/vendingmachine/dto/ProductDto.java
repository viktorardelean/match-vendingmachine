package com.vardelean.vendingmachine.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class ProductDto {

  private Long id;

  @NonNull @NotNull @PositiveOrZero private Long amountAvailable;

  @NonNull @NotNull @Positive private Long cost;

  @NonNull @NotEmpty private String productName;

  @NonNull @NotNull @Positive private Long sellerId;
}

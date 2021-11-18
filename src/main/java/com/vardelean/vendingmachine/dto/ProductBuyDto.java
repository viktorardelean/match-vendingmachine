package com.vardelean.vendingmachine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductBuyDto {

  @NotNull private Long productId;

  @NotNull @Positive private Long requestedAmount;
}

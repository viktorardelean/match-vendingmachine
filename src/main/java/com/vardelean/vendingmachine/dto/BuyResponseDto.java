package com.vardelean.vendingmachine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.AbstractMap;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyResponseDto {

  private Long totalSpent;
  private Optional<ProductBuyDto> productsPurchased;
  private List<AbstractMap.SimpleImmutableEntry<Long, Long>> change;
}

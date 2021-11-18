package com.vardelean.vendingmachine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

  private Long id;
  private Long amountAvailable;
  private Long cost;
  private String productName;
  private Long sellerId;
}

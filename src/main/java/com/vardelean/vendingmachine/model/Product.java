package com.vardelean.vendingmachine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Long amountAvailable;
  private Long cost;

  @Column(unique = true, nullable = false)
  private String productName;

  @ManyToOne
  @JoinColumn(name = "seller_id", nullable = false)
  private VendingMachineUser seller;
}

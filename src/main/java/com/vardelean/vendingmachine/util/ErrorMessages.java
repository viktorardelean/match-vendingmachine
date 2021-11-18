package com.vardelean.vendingmachine.util;

public interface ErrorMessages {
  String ERROR_INVALID_COINS = "Invalid coins inserted: ";
  String ERROR_INSUFFICIENT_DEPOSIT = "Insufficient deposit funds: ";
  String ERROR_INSUFFICIENT_AMOUNT = "Insufficient product amount: ";
  String ERROR_INVALID_PRODUCT_ID = "Invalid product id: ";
  String ERROR_INVALID_USER_ID = "Invalid user id: ";
  String ERROR_CANNOT_ASSIGN_PRODUCT_TO_BUYER =
      "Cannot assign a product to a user that is not a seller: ";
}

package com.vardelean.vendingmachine.exception;

public class NotFoundException extends Exception {
    private static final long serialVersionUID = -6025384522508236612L;

    public NotFoundException(String errorMessage) {
    super(errorMessage);
  }
}

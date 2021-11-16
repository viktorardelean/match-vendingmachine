package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.model.AuthenticationRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
  ResponseEntity<?> authenticate(AuthenticationRequest authenticationRequest);
}

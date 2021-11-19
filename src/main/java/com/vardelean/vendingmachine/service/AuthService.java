package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.model.AuthenticationRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthService {
  void authenticate(AuthenticationRequest authenticationRequest, HttpServletResponse response)
      throws IOException;

  void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}

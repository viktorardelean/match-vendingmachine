package com.vardelean.vendingmachine.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.vardelean.vendingmachine.model.AuthenticationRequest;
import com.vardelean.vendingmachine.util.HttpUtil;
import com.vardelean.vendingmachine.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final JwtUtil jwtUtil;
  private final HttpUtil httpUtil;

  @Override
  public void authenticate(
      AuthenticationRequest authenticationRequest, HttpServletResponse response)
      throws IOException {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authenticationRequest.getUsername(), authenticationRequest.getPassword()));
      final UserDetails userDetails =
          userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

      httpUtil.sendTokenResponse(response, jwtUtil.generateToken(userDetails));
    } catch (Exception e) {
      httpUtil.sendErrorResponse(response, e);
    }
  }

  @Override
  public void refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    final String authorizationHeader = request.getHeader(AUTHORIZATION);
    Optional<String> refreshToken = jwtUtil.extractToken(authorizationHeader);
    if (refreshToken.isPresent()) {
      try {
        DecodedJWT decodedJWT = jwtUtil.decodeToken(refreshToken.get());
        final String username = decodedJWT.getSubject();
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        httpUtil.sendTokenResponse(response, jwtUtil.generateToken(userDetails));
      } catch (Exception e) {
        log.error("Error logging in: {}", e.getMessage());
        httpUtil.sendErrorResponse(response, e);
      }
    } else {
      throw new RuntimeException("Refresh token is missing");
    }
  }
}

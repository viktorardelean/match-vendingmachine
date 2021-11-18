package com.vardelean.vendingmachine.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vardelean.vendingmachine.model.AuthenticationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
public class HttpUtil {

  public void sendErrorResponse(HttpServletResponse response, Exception e) throws IOException {
    response.setHeader("error", e.getMessage());
    response.setContentType(APPLICATION_JSON_VALUE);
    response.setStatus(FORBIDDEN.value());
    Map<String, String> error = new HashMap<>();
    error.put("error", e.getMessage());
    new ObjectMapper().writeValue(response.getOutputStream(), error);
  }

  public void sendTokenResponse(
      HttpServletResponse response, AuthenticationResponse authenticationResponse)
      throws IOException {
    response.setContentType(APPLICATION_JSON_VALUE);
    new ObjectMapper().writeValue(response.getOutputStream(), authenticationResponse);
  }

  public RuntimeException badRequest(Long value, String message) {
    return badRequest(String.valueOf(value), message);
  }

  public RuntimeException badRequest(Map value, String message) {
    return badRequest(String.valueOf(value), message);
  }

  public RuntimeException badRequest(String value, String message) {
    log.error(message + "{}", value);
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message + value);
  }
}

package com.vardelean.vendingmachine.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.vardelean.vendingmachine.util.HttpUtil;
import com.vardelean.vendingmachine.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final HttpUtil httpUtil;

  @Override
  protected void doFilterInternal(
      @NonNull final HttpServletRequest request,
      @NonNull final HttpServletResponse response,
      @NonNull final FilterChain filterChain)
      throws ServletException, IOException {

    if (request.getServletPath().equals("/api/token/refresh")) {
      filterChain.doFilter(request, response);
    } else {
      final String authorizationHeader = request.getHeader(AUTHORIZATION);
      Optional<String> token = jwtUtil.extractToken(authorizationHeader);
      if (token.isPresent()) {
        try {
          DecodedJWT decodedJWT = jwtUtil.decodeToken(token.get());
          final String username = decodedJWT.getSubject();
          UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
              new UsernamePasswordAuthenticationToken(
                  username, null, jwtUtil.extractClaims(decodedJWT));
          usernamePasswordAuthenticationToken.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        } catch (Exception e) {
          log.error("Error logging in: {}", e.getMessage());
          httpUtil.sendErrorResponse(response, e);
        }
      }
      filterChain.doFilter(request, response);
    }
  }
}

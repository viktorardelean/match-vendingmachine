package com.vardelean.vendingmachine.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vardelean.vendingmachine.model.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class JwtUtil {

  private static final String SECRET_KEY = "secret";
  private static final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
  private static final String ISSUER = "Vending Machine App";
  private static final Integer VALIDITY_ACCESS_TOKEN = 10 * 60 * 1000;
  private static final Integer VALIDITY_REFRESH_TOKEN = 30 * 60 * 1000;
  private static final String CLAIMS_KEY = "roles";

  public Collection<SimpleGrantedAuthority> extractClaims(DecodedJWT decodedToken) {
    List<String> roles = decodedToken.getClaim(CLAIMS_KEY).asList(String.class);
    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority((role))));
    return authorities;
  }

  public AuthenticationResponse generateToken(UserDetails userDetails) {

    String accessToken =
        JWT.create()
            .withSubject(userDetails.getUsername())
            .withIssuer(ISSUER)
            .withIssuedAt(new Date(System.currentTimeMillis()))
            .withExpiresAt(new Date(System.currentTimeMillis() + VALIDITY_ACCESS_TOKEN))
            .withClaim(
                CLAIMS_KEY,
                userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()))
            .sign(algorithm);

    String refreshToken =
        JWT.create()
            .withSubject(userDetails.getUsername())
            .withIssuer(ISSUER)
            .withIssuedAt(new Date(System.currentTimeMillis()))
            .withExpiresAt(new Date(System.currentTimeMillis() + VALIDITY_REFRESH_TOKEN))
            .sign(algorithm);
    return new AuthenticationResponse(accessToken, refreshToken);
  }

  public DecodedJWT validateToken(String token) {
    final JWTVerifier verifier = JWT.require(algorithm).build();
    return verifier.verify(token);
  }
}

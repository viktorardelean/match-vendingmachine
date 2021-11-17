package com.vardelean.vendingmachine.api;

import com.vardelean.vendingmachine.dto.VendingMachineUserDto;
import com.vardelean.vendingmachine.model.AuthenticationRequest;
import com.vardelean.vendingmachine.model.Role;
import com.vardelean.vendingmachine.model.VendingMachineUser;
import com.vardelean.vendingmachine.service.AuthService;
import com.vardelean.vendingmachine.service.VendingMachineUserService;
import com.vardelean.vendingmachine.util.JwtUtil;
import javassist.tools.web.BadHttpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserResource {
  private final VendingMachineUserService vendingMachineUserService;
  private final AuthService authService;
  private final JwtUtil jwtUtil;

  @PostMapping("/authenticate")
  public void createAuthenticationToken(
      @RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response)
      throws IOException {
    authService.authenticate(authenticationRequest, response);
  }

  @GetMapping("/token/refresh")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    authService.refreshToken(request, response);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<VendingMachineUserDto> getUser(@PathVariable Long userId)
      throws BadHttpRequest {
    return ResponseEntity.ok().body(vendingMachineUserService.getUser(userId));
  }

  @GetMapping("/users")
  public ResponseEntity<List<VendingMachineUser>> getUsers() {
    return ResponseEntity.ok().body(vendingMachineUserService.getUsers());
  }

  @PostMapping("/user")
  public ResponseEntity<VendingMachineUserDto> registerUser(
      @RequestBody VendingMachineUserDto vendingMachineUserDto) throws BadHttpRequest {
    return ResponseEntity.ok().body(vendingMachineUserService.saveUser(vendingMachineUserDto));
  }

  @PutMapping("/user/{userId}")
  public ResponseEntity<VendingMachineUserDto> updateUser(
      @PathVariable Long userId, @RequestBody VendingMachineUserDto vendingMachineUserDto) {
    return ResponseEntity.ok()
        .body(vendingMachineUserService.updateUser(userId, vendingMachineUserDto));
  }

  @DeleteMapping("/user/{userId}")
  public void deleteUser(@PathVariable Long userId) {
    vendingMachineUserService.deleteUser(userId);
  }

  @PatchMapping("/reset")
  public void resetDeposit(@RequestHeader(name = "Authorization") String authorizationHeader) {
    Optional<String> jwt = jwtUtil.extractToken(authorizationHeader);
    String username = jwtUtil.extractUsername(jwtUtil.decodeToken(jwt.get()));
    vendingMachineUserService.resetDeposit(username);
  }

  @PostMapping("/role")
  public ResponseEntity<Role> registerRole(@RequestBody Role role) {
    URI uri =
        URI.create(
            ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/role/register")
                .toUriString());
    return ResponseEntity.created(uri).body(vendingMachineUserService.saveRole(role));
  }

  @GetMapping("/roles")
  public ResponseEntity<List<Role>> getRoles() {
    return ResponseEntity.ok().body(vendingMachineUserService.getRoles());
  }
}

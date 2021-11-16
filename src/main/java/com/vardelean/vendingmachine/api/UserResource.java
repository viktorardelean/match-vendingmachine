package com.vardelean.vendingmachine.api;

import com.vardelean.vendingmachine.dto.VendingMachineUserDto;
import com.vardelean.vendingmachine.model.AuthenticationRequest;
import com.vardelean.vendingmachine.model.Role;
import com.vardelean.vendingmachine.model.VendingMachineUser;
import com.vardelean.vendingmachine.service.AuthService;
import com.vardelean.vendingmachine.service.VendingMachineUserService;
import javassist.tools.web.BadHttpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserResource {
  private final VendingMachineUserService vendingMachineUserService;
  private final AuthService authService;

  @PostMapping("/authenticate")
  public ResponseEntity<?> createAuthenticationToken(
      @RequestBody AuthenticationRequest authenticationRequest) {

    return authService.authenticate(authenticationRequest);
  }

  @GetMapping("/user/{username}")
  public ResponseEntity<VendingMachineUser> getUser(@PathVariable String username)
      throws BadHttpRequest {
    return ResponseEntity.ok().body(vendingMachineUserService.getUser(username));
  }

  @GetMapping("/users")
  public ResponseEntity<List<VendingMachineUser>> getUsers() {
    return ResponseEntity.ok().body(vendingMachineUserService.getUsers());
  }

  @GetMapping("/roles")
  public ResponseEntity<List<Role>> getRoles() {
    return ResponseEntity.ok().body(vendingMachineUserService.getRoles());
  }

  @PostMapping("/user")
  public ResponseEntity<VendingMachineUser> registerUser(
      @RequestBody VendingMachineUserDto vendingMachineUserDto) throws BadHttpRequest {
    URI uri =
        URI.create(
            ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/user/register")
                .toUriString());
    return ResponseEntity.created(uri)
        .body(vendingMachineUserService.saveUser(vendingMachineUserDto));
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
}

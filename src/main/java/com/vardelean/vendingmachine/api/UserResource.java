package com.vardelean.vendingmachine.api;

import com.vardelean.vendingmachine.model.VendingMachineUser;
import com.vardelean.vendingmachine.service.VendingMachineUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserResource {
  private final VendingMachineUserService vendingMachineUserService;

  @GetMapping("/user/{username}")
  public ResponseEntity<VendingMachineUser> getUser(@RequestAttribute String userName) {
    return ResponseEntity.ok().body(vendingMachineUserService.getUser(userName));
  }
}

package com.vardelean.vendingmachine;

import com.vardelean.vendingmachine.dto.VendingMachineUserDto;
import com.vardelean.vendingmachine.model.Role;
import com.vardelean.vendingmachine.service.VendingMachineUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class VendingmachineApplication {

  public static void main(String[] args) {
    SpringApplication.run(VendingmachineApplication.class, args);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  CommandLineRunner run(VendingMachineUserService vendingMachineUserService) {
    return args -> {
      vendingMachineUserService.saveRole(new Role(null, "ROLE_BUYER"));
      vendingMachineUserService.saveRole(new Role(null, "ROLE_SELLER"));

      vendingMachineUserService.saveUser(
          new VendingMachineUserDto("user1", "pass1", 100L, "ROLE_BUYER"));
      vendingMachineUserService.saveUser(
          new VendingMachineUserDto("user2", "pass2", 0L, "ROLE_SELLER"));
    };
  }
}

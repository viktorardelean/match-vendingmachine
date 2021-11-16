package com.vardelean.vendingmachine.repo;

import com.vardelean.vendingmachine.model.VendingMachineUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendingMachineUserRepo extends JpaRepository<VendingMachineUser, Long> {
  Optional<VendingMachineUser> findByUsername(String username);
}

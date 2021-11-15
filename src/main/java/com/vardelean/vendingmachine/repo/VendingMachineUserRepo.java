package com.vardelean.vendingmachine.repo;

import com.vardelean.vendingmachine.model.VendingMachineUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendingMachineUserRepo extends JpaRepository<VendingMachineUser, Long> {
  VendingMachineUser findByUsername(String username);
}

package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.model.Role;
import com.vardelean.vendingmachine.model.VendingMachineUser;

public interface VendingMachineUserService {

  VendingMachineUser saveUser(VendingMachineUser user);

  Role saveRole(Role role);
}

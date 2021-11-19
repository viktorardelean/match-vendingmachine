package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.dto.VendingMachineUserDto;
import com.vardelean.vendingmachine.model.Role;
import com.vardelean.vendingmachine.model.VendingMachineUser;

import java.util.List;

public interface VendingMachineUserService {

  VendingMachineUserDto saveUser(VendingMachineUserDto user);

  Role saveRole(Role role);

  VendingMachineUserDto getUserById(Long userId);

  VendingMachineUser getUserByUsername(String username);

  List<Role> getRoles();

  List<VendingMachineUserDto> getUsers();

  VendingMachineUserDto updateUser(Long userId, VendingMachineUserDto vendingMachineUserDto);

  void deleteUser(Long userId);

  void resetDeposit(String username);
}

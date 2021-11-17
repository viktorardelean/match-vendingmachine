package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.dto.VendingMachineUserDto;
import com.vardelean.vendingmachine.model.Role;
import com.vardelean.vendingmachine.model.VendingMachineUser;
import javassist.tools.web.BadHttpRequest;

import java.util.List;

public interface VendingMachineUserService {

  VendingMachineUserDto saveUser(VendingMachineUserDto user) throws BadHttpRequest;

  Role saveRole(Role role);

  VendingMachineUserDto getUser(Long userId) throws BadHttpRequest;

  List<Role> getRoles();

  List<VendingMachineUser> getUsers();

  VendingMachineUserDto updateUser(Long userId, VendingMachineUserDto vendingMachineUserDto);

  void deleteUser(Long userId);
}

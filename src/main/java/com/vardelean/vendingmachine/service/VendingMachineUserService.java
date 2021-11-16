package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.dto.VendingMachineUserDto;
import com.vardelean.vendingmachine.model.Role;
import com.vardelean.vendingmachine.model.VendingMachineUser;
import javassist.tools.web.BadHttpRequest;

import java.util.List;

public interface VendingMachineUserService {

  VendingMachineUser saveUser(VendingMachineUserDto user) throws BadHttpRequest;

  Role saveRole(Role role);

  VendingMachineUser getUser(String username) throws BadHttpRequest;

  List<Role> getRoles();

  List<VendingMachineUser> getUsers();
}

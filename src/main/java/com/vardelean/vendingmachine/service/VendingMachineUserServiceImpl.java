package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.model.Role;
import com.vardelean.vendingmachine.model.VendingMachineUser;
import com.vardelean.vendingmachine.repo.RoleRepo;
import com.vardelean.vendingmachine.repo.VendingMachineUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VendingMachineUserServiceImpl implements VendingMachineUserService {

  private final VendingMachineUserRepo vendingMachineUserRepo;
  private final RoleRepo roleRepo;

  @Override
  public VendingMachineUser saveUser(VendingMachineUser user) {
    log.info("Save user to DB: {}", user);
    return vendingMachineUserRepo.save(user);
  }

  @Override
  public Role saveRole(Role role) {
    log.info("Save role to DB: {}", role);
    return roleRepo.save(role);
  }

  @Override
  public void addRoleToUser(String username, String roleName) {
    log.info("Add role {} to user {}", roleName, username);
    VendingMachineUser user = vendingMachineUserRepo.findByUsername(username);
    Role role = roleRepo.findByName(roleName);
    user.getRoles().add(role);
  }

  @Override
  public VendingMachineUser getUser(String username) {
    log.info("Read user from DB: {}", username);
    return vendingMachineUserRepo.findByUsername(username);
  }
}

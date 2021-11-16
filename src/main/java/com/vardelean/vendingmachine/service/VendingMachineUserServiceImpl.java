package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.converter.VendingMachineUserConverter;
import com.vardelean.vendingmachine.dto.VendingMachineUserDto;
import com.vardelean.vendingmachine.exception.NotFoundException;
import com.vardelean.vendingmachine.model.Role;
import com.vardelean.vendingmachine.model.VendingMachineUser;
import com.vardelean.vendingmachine.repo.RoleRepo;
import com.vardelean.vendingmachine.repo.VendingMachineUserRepo;
import javassist.tools.web.BadHttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class VendingMachineUserServiceImpl implements VendingMachineUserService {

  private final VendingMachineUserRepo vendingMachineUserRepo;
  private final RoleRepo roleRepo;
  private final VendingMachineUserConverter vendingMachineUserConverter;

  @Override
  public VendingMachineUser saveUser(@NonNull VendingMachineUserDto vendingMachineUserDto)
      throws BadHttpRequest {
    VendingMachineUser vendingMachineUser =
        vendingMachineUserConverter.toEntity(vendingMachineUserDto);
    vendingMachineUser.getRoles().add(getRole(vendingMachineUserDto.getRoleName()));

    log.info("Save user to DB: {}", vendingMachineUser);
    return vendingMachineUserRepo.save(vendingMachineUser);
  }

  @Override
  public Role saveRole(@NonNull Role role) {
    log.info("Save role to DB: {}", role);
    return roleRepo.save(role);
  }

  @Override
  public VendingMachineUser getUser(@NonNull String username) throws BadHttpRequest {
    log.info("Read user from DB: {}", username);
    Optional<VendingMachineUser> userOptional = vendingMachineUserRepo.findByUsername(username);
    return userOptional.orElseThrow(
        () -> new BadHttpRequest(new NotFoundException("User does not exists: " + username)));
  }

  @Override
  public List<Role> getRoles() {
    return roleRepo.findAll();
  }

  @Override
  public List<VendingMachineUser> getUsers() {
    return vendingMachineUserRepo.findAll();
  }

  private Role getRole(@NonNull String roleName) throws BadHttpRequest {
    log.info("Read role from DB {}", roleName);
    Optional<Role> roleOptional = roleRepo.findByName(roleName);
    return roleOptional.orElseThrow(
        () -> new BadHttpRequest(new NotFoundException("Role does not exists: " + roleName)));
  }
}

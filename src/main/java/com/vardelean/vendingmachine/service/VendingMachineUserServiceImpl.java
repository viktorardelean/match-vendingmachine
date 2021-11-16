package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.converter.VendingMachineUserConverter;
import com.vardelean.vendingmachine.dto.VendingMachineUserDto;
import com.vardelean.vendingmachine.model.Role;
import com.vardelean.vendingmachine.model.VendingMachineUser;
import com.vardelean.vendingmachine.repo.RoleRepo;
import com.vardelean.vendingmachine.repo.VendingMachineUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class VendingMachineUserServiceImpl
    implements VendingMachineUserService, UserDetailsService {

  private final VendingMachineUserRepo vendingMachineUserRepo;
  private final RoleRepo roleRepo;
  private final VendingMachineUserConverter vendingMachineUserConverter;

  @Override
  public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
    Optional<VendingMachineUser> vendingMachineUser =
        vendingMachineUserRepo.findByUsername(username);
    return vendingMachineUser
        .map(
            user -> {
              Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
              user.getRoles()
                  .forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
              return new User(user.getUsername(), user.getPassword(), authorities);
            })
        .orElseThrow(
            () -> {
              log.error("User not found in the DB: {}", username);
              return new UsernameNotFoundException("User does not exists: " + username);
            });
  }

  @Override
  public VendingMachineUser saveUser(@NonNull VendingMachineUserDto vendingMachineUserDto) {
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
  public VendingMachineUser getUser(@NonNull String username) {
    log.info("Read user from DB: {}", username);
    Optional<VendingMachineUser> vendingMachineUser =
        vendingMachineUserRepo.findByUsername(username);
    return vendingMachineUser.orElseThrow(
        () -> {
          log.error("User not found in the DB: {}", username);
          return new ResponseStatusException(
              HttpStatus.BAD_REQUEST, "User does not exists: " + username);
        });
  }

  @Override
  public List<Role> getRoles() {
    return roleRepo.findAll();
  }

  @Override
  public List<VendingMachineUser> getUsers() {
    return vendingMachineUserRepo.findAll();
  }

  private Role getRole(@NonNull String roleName) {
    log.info("Read role from DB {}", roleName);
    Optional<Role> role = roleRepo.findByName(roleName);
    return role.orElseThrow(
        () -> {
          log.error("Role not found in the DB: {}", roleName);
          return new ResponseStatusException(
              HttpStatus.BAD_REQUEST, "Role does not exists: " + roleName);
        });
  }
}

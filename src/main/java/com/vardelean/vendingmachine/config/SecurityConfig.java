package com.vardelean.vendingmachine.config;

import com.vardelean.vendingmachine.filter.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String ROLE_SELLER = "ROLE_SELLER";
  private static final String ROLE_BUYER = "ROLE_BUYER";
  private final UserDetailsService userDetailsService;
  private final JwtRequestFilter jwtRequestFilter;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder)
      throws Exception {
    authenticationManagerBuilder
        .userDetailsService(userDetailsService)
        .passwordEncoder(bCryptPasswordEncoder);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();
    http.sessionManagement().sessionCreationPolicy(STATELESS);
    http.authorizeRequests()
        .antMatchers("/api/authenticate/**", "/api/token/refresh/**")
        .permitAll();
    http.authorizeRequests().antMatchers(POST, "/api/user/**").permitAll();
    http.authorizeRequests().antMatchers(POST, "/api/users/**").permitAll();
    http.authorizeRequests().antMatchers(POST, "/api/roles/**").permitAll();
    http.authorizeRequests().antMatchers(POST, "/api/product/**").hasAnyAuthority(ROLE_SELLER);
    http.authorizeRequests().antMatchers(PUT, "/api/product/**").hasAnyAuthority(ROLE_SELLER);
    http.authorizeRequests().antMatchers(DELETE, "/api/product/**").hasAnyAuthority(ROLE_SELLER);
    http.authorizeRequests().antMatchers(PATCH, "/api/deposit/**").hasAnyAuthority(ROLE_BUYER);
    http.authorizeRequests().antMatchers(POST, "/api/buy/**").hasAnyAuthority(ROLE_BUYER);
    http.authorizeRequests().antMatchers(PATCH, "/api/reset/**").hasAnyAuthority(ROLE_BUYER);
    http.authorizeRequests().anyRequest().authenticated();

    http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
}

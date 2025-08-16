package org.example.authservice.service;

import org.example.authservice.repository.IUserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserDetailService implements ReactiveUserDetailsService {
  private final IUserRepository repository;

  public UserDetailService(IUserRepository repository) {
    this.repository = repository;
  }

  @Override
  public Mono<UserDetails> findByUsername(String username) {
    return repository.findByUsername(username)
      .map(user -> User
        .builder()
        .username(user.getUsername())
        .password(user.getPassword())
        .roles(user.getRole())
        .build()
      );
  }
}

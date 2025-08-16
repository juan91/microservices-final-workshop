package org.example.authservice.repository;

import org.example.authservice.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IUserRepository extends ReactiveCrudRepository<User, Long> {
  Mono<User> findByUsername(String username);
}
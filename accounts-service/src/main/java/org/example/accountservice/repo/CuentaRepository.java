package org.example.accountservice.repo;

import org.example.accountservice.model.Cuenta;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CuentaRepository extends ReactiveCrudRepository<Cuenta, Long> {

    Mono<Cuenta> findByNumero(long numeroCuenta);
}
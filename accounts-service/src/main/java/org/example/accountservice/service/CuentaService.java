package org.example.accountservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.accountservice.grpc.TransactionConsumer;
import org.example.accountservice.model.Cuenta;
import org.example.accountservice.repo.CuentaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CuentaService {


    private final CuentaRepository cuentaRepository;
    private final TransactionConsumer transactionConsumer;
    private final ObjectMapper objectMapper;

    private final WebClient webClient = WebClient.create("http://localhost:8080");

    public CuentaService(CuentaRepository cuentaRepository, TransactionConsumer transactionConsumer, ObjectMapper objectMapper) {
        this.cuentaRepository = cuentaRepository;
        this.transactionConsumer = transactionConsumer;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> getTransactions(Long transactionId) throws Exception {
        String json = transactionConsumer.getTransactionsJson(transactionId);
        return objectMapper.readValue(json, Map.class);
    }

    public Mono<Cuenta> crearCuenta(Cuenta cuenta, String authHeader) {
        cuenta.setCreatedAt(LocalDateTime.now());
        return webClient.get()
                .uri("/api/banks/{id}", cuenta.getBancoId())
                .headers(headers -> headers.set("Authorization", authHeader))
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        clientResponse -> Mono.error(
                                new ResponseStatusException(HttpStatus.NOT_FOUND, "El banco no existe con id: " + cuenta.getBancoId())
                        )
                )
                .bodyToMono(String.class)
                .flatMap(response -> cuentaRepository.save(cuenta)
                        .onErrorMap(error -> {
                            System.out.println("Error al registrar la cuenta: "+error.getMessage());
                            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar la cuenta: " + error.getMessage());
                        }));
    }

    public Flux<Cuenta> listarCuentas() {
        return cuentaRepository.findAll();
    }

    public Mono<Cuenta> obtenerCuenta(Long id) {
        return cuentaRepository.findByNumero(id)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada con id: " + id)
                ));
    }

    public Mono<Cuenta> actualizarCuenta(Long id, Cuenta cuenta) {
        cuenta.setCreatedAt(LocalDateTime.now());
        cuenta.setId(id);
        return cuentaRepository.save(cuenta);
    }

    public Mono<Void> eliminarCuenta(Long id) {
        return cuentaRepository.deleteById(id);
    }
}
package org.example.bankservice.service;

import org.example.bankservice.model.Bank;
import org.example.bankservice.repo.BankRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BankService {
    private final BankRepository bankRepository;

    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public Flux<Bank> findAll() {
        return bankRepository.findAll();
    }

    public Mono<Bank> findById(int id) {
        return bankRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Banco no encontrada con id: " + id)
                ));
    }

    public Mono<Bank> save(Bank bank) {
        return bankRepository.save(bank);
    }

    public Mono<Void> deleteById(int id) {
        return bankRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Banco no encontrado con id: " + id)
                ))
                .flatMap(bank -> bankRepository.deleteById(id));
    }
}
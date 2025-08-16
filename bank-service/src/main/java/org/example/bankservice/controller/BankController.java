package org.example.bankservice.controller;

import org.example.bankservice.model.Bank;
import org.example.bankservice.service.BankService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestController
@RequestMapping("/api/banks")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    public Flux<Bank> getAllBanks() {
        return bankService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Bank> getBankById(@PathVariable int id) {
        return bankService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Bank> createBank(@RequestBody Bank bank) {
        bank.setCreatedAt(Instant.now());
        return bankService.save(bank);
    }

    @PutMapping("/{id}")
    public Mono<Bank> updateBank(@PathVariable int id, @RequestBody Bank bank) {
        return bankService.findById(id)
                .flatMap(existingBank -> {
                    existingBank.setName(bank.getName());
                    existingBank.setDescription(bank.getDescription());
                    return bankService.save(existingBank);
                });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> deleteBank(@PathVariable int id) {
        return bankService.deleteById(id);
    }
}
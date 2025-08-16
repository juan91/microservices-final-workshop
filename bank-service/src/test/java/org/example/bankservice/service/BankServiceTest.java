package org.example.bankservice.service;

import org.example.bankservice.model.Bank;
import org.example.bankservice.repo.BankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BankServiceTest {

    private BankRepository bankRepository;
    private BankService bankService;

    @BeforeEach
    void setUp() {
        bankRepository = Mockito.mock(BankRepository.class);
        bankService = new BankService(bankRepository);
    }

    @Test
    void findAll_ShouldReturnFluxBanks() {
        Bank bank1 = new Bank(1, "Bancolombia","description1", Instant.now());
        Bank bank2 = new Bank(1, "AV","description1", Instant.now());

        when(bankRepository.findAll()).thenReturn(Flux.just(bank1, bank2));

        StepVerifier.create(bankService.findAll())
                .expectNext(bank1)
                .expectNext(bank2)
                .verifyComplete();

        verify(bankRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnBank_WhenExists() {
        Bank bank = new Bank(1, "Bancolombia","description1", Instant.now());
        when(bankRepository.findById(1)).thenReturn(Mono.just(bank));

        StepVerifier.create(bankService.findById(1))
                .expectNext(bank)
                .verifyComplete();

        verify(bankRepository, times(1)).findById(1);
    }

    @Test
    void findById_ShouldThrow_WhenNotFound() {
        when(bankRepository.findById(99)).thenReturn(Mono.empty());

        StepVerifier.create(bankService.findById(99))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof ResponseStatusException);
                    ResponseStatusException ex = (ResponseStatusException) error;
                    // En Spring Boot 3, getStatusCode() devuelve HttpStatusCode
                    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
                    // opcional: validar el mensaje
                    assertTrue(ex.getReason().contains("Banco no encontrada"));
                })
                .verify();

        verify(bankRepository).findById(99);
    }

    @Test
    void save_ShouldReturnSavedBank() {
        Bank bank = new Bank(1, "Bancolombia","description1", Instant.now());
        when(bankRepository.save(any(Bank.class))).thenReturn(Mono.just(bank));

        StepVerifier.create(bankService.save(bank))
                .expectNext(bank)
                .verifyComplete();

        verify(bankRepository, times(1)).save(bank);
    }

    @Test
    void deleteById_ShouldDelete_WhenExists() {
        Bank bank = new Bank(1, "Bancolombia","description1", Instant.now());
        when(bankRepository.findById(1)).thenReturn(Mono.just(bank));
        when(bankRepository.deleteById(1)).thenReturn(Mono.empty());

        StepVerifier.create(bankService.deleteById(1))
                .verifyComplete();

        verify(bankRepository, times(1)).findById(1);
        verify(bankRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteById_ShouldThrow_WhenNotFound() {
        when(bankRepository.findById(99)).thenReturn(Mono.empty());

        StepVerifier.create(bankService.deleteById(99))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof ResponseStatusException);
                    assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) error).getStatusCode());
                })
                .verify();

        verify(bankRepository, times(1)).findById(99);
        verify(bankRepository, never()).deleteById(anyInt());
    }

}

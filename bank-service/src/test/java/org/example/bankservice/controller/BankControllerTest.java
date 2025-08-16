package org.example.bankservice.controller;

import org.example.bankservice.model.Bank;
import org.example.bankservice.service.BankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.Instant;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BankControllerTest {

    @Mock
    private BankService bankService;

    @InjectMocks
    private BankController bankController;

    private Bank bank;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bank = new Bank();
        bank.setId(1);
        bank.setName("Bancolombia");
        bank.setDescription("Banco grande en Colombia");
        bank.setCreatedAt(Instant.now());
    }

    @Test
    void testGetAllBanks() {
        when(bankService.findAll()).thenReturn(Flux.just(bank));

        StepVerifier.create(bankController.getAllBanks())
                .expectNext(bank)
                .verifyComplete();

        verify(bankService, times(1)).findAll();
    }

    @Test
    void testGetBankById() {
        when(bankService.findById(1)).thenReturn(Mono.just(bank));

        StepVerifier.create(bankController.getBankById(1))
                .expectNext(bank)
                .verifyComplete();

        verify(bankService, times(1)).findById(1);
    }

    @Test
    void testCreateBank() {
        when(bankService.save(any(Bank.class))).thenReturn(Mono.just(bank));

        StepVerifier.create(bankController.createBank(bank))
                .expectNextMatches(createdBank -> createdBank.getName().equals("Bancolombia"))
                .verifyComplete();

        verify(bankService, times(1)).save(any(Bank.class));
    }

    @Test
    void testUpdateBank() {
        Bank updated = new Bank();
        updated.setId(1);
        updated.setName("Banco Actualizado");
        updated.setDescription("Descripción nueva");

        when(bankService.findById(1)).thenReturn(Mono.just(bank));
        when(bankService.save(any(Bank.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(bankController.updateBank(1, updated))
                .expectNextMatches(b -> b.getName().equals("Banco Actualizado"))
                .verifyComplete();

        verify(bankService, times(1)).findById(1);
        verify(bankService, times(1)).save(any(Bank.class));
    }

    @Test
    void testDeleteBank() {
        when(bankService.deleteById(1)).thenReturn(Mono.empty());

        StepVerifier.create(bankController.deleteBank(1))
                .verifyComplete();

        verify(bankService, times(1)).deleteById(1);
    }
}

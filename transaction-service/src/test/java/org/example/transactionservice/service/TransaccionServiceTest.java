package org.example.transactionservice.service;

import org.example.transactionservice.model.Cuenta;
import org.example.transactionservice.model.Transactions;
import org.example.transactionservice.repo.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TransaccionService transaccionService;

    private Cuenta cuentaOrigen;
    private Cuenta cuentaDestino;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cuentaOrigen = new Cuenta(1L, 1001L, "Juan",
                BigDecimal.valueOf(1000), 10L, LocalDateTime.now());

        cuentaDestino = new Cuenta(2L, 2001L, "Pedro",
                BigDecimal.valueOf(500), 10L, LocalDateTime.now());

        // mock repository → devuelve lo que le pasen
        when(transaccionRepository.save(any(Transactions.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
    }

    @Test
    void procesarIntraBancaria_exitoso() {
        StepVerifier.create(transaccionService
                        .procesarIntraBancaria(cuentaOrigen, cuentaDestino, BigDecimal.valueOf(200)))
                .expectNextMatches(tx -> tx.getTipo().equals("DEPOSITO") &&
                        tx.getMonto().compareTo(BigDecimal.valueOf(200)) == 0)
                .verifyComplete();

        verify(transaccionRepository, times(2)).save(any(Transactions.class));
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void procesarIntraBancaria_fondosInsuficientes() {
        cuentaOrigen.setSaldo(BigDecimal.valueOf(100)); // menos que el monto

        StepVerifier.create(transaccionService
                        .procesarIntraBancaria(cuentaOrigen, cuentaDestino, BigDecimal.valueOf(500)))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Fondos insuficientes"))
                .verify();

        verify(transaccionRepository, never()).save(any());
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void procesarInterBancaria_exitoso() {
        cuentaDestino.setBancoId(20L); // banco distinto
        BigDecimal monto = BigDecimal.valueOf(500);

        StepVerifier.create(transaccionService
                        .procesarInterBancaria(cuentaOrigen, cuentaDestino, monto))
                .expectNextMatches(tx ->
                        tx.getImpuesto().compareTo(BigDecimal.valueOf(5)) == 0 &&
                                tx.getMonto().compareTo(BigDecimal.valueOf(500)) == 0)
                .verifyComplete();

        verify(transaccionRepository, times(1)).save(any(Transactions.class));
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq("transaction-exchange"), eq("transaction-routing-key"), any(Transactions.class));
    }

    @Test
    void procesarInterBancaria_fondosInsuficientes() {
        cuentaDestino.setBancoId(20L); // banco distinto
        cuentaOrigen.setSaldo(BigDecimal.valueOf(100)); // no alcanza

        StepVerifier.create(transaccionService
                        .procesarInterBancaria(cuentaOrigen, cuentaDestino, BigDecimal.valueOf(500)))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Fondos insuficientes"))
                .verify();

        verify(transaccionRepository, never()).save(any());
        verify(rabbitTemplate, never())
                .convertAndSend(anyString(), anyString(), any(Transactions.class));
    }
}

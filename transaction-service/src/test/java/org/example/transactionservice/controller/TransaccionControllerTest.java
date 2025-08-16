package org.example.transactionservice.controller;

import org.example.transactionservice.dto.TransferRequest;
import org.example.transactionservice.model.Transactions;
import org.example.transactionservice.service.TransaccionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransaccionControllerTest {

    @Mock
    private TransaccionService transaccionService;

    @InjectMocks
    private TransaccionController transaccionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void transferir_ShouldReturnOk_WhenServiceSucceeds() {
        // given
        TransferRequest request = new TransferRequest();
        request.setCuentaOrigenId(1L);
        request.setCuentaDestinoId(2L);
        request.setMonto(new java.math.BigDecimal("100.00"));

        Transactions tx = new Transactions();
        tx.setId(123L);

        when(transaccionService.transferir(anyLong(), anyLong(), any(), anyString()))
                .thenReturn(Mono.just(tx));

        // when
        Mono<ResponseEntity<Transactions>> result =
                transaccionController.transferir(request, "Bearer token");

        // then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(200, response.getStatusCodeValue());
                    assertEquals(tx, response.getBody());
                })
                .verifyComplete();

        verify(transaccionService, times(1))
                .transferir(eq(1L), eq(2L), eq(request.getMonto()), eq("Bearer token"));
    }

    @Test
    void transferir_ShouldReturnBadRequest_WhenServiceFails() {
        // given
        TransferRequest request = new TransferRequest();
        request.setCuentaOrigenId(1L);
        request.setCuentaDestinoId(2L);
        request.setMonto(new java.math.BigDecimal("100.00"));

        when(transaccionService.transferir(anyLong(), anyLong(), any(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Service failed")));

        // when
        Mono<ResponseEntity<Transactions>> result =
                transaccionController.transferir(request, "Bearer token");

        // then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(400, response.getStatusCodeValue());
                    assertEquals(null, response.getBody());
                })
                .verifyComplete();

        verify(transaccionService, times(1))
                .transferir(eq(1L), eq(2L), eq(request.getMonto()), eq("Bearer token"));
    }
}

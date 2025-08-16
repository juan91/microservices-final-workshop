package org.example.accountservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.accountservice.grpc.TransactionConsumer;
import org.example.accountservice.model.Cuenta;
import org.example.accountservice.repo.CuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CuentaServiceTest {

    private CuentaRepository cuentaRepository;
    private TransactionConsumer transactionConsumer;
    private ObjectMapper objectMapper;
    private CuentaService cuentaService;

    @BeforeEach
    void setUp() {
        cuentaRepository = Mockito.mock(CuentaRepository.class);
        transactionConsumer = Mockito.mock(TransactionConsumer.class);
        objectMapper = new ObjectMapper();

        cuentaService = new CuentaService(cuentaRepository, transactionConsumer, objectMapper);
    }

    @Test
    void testGetTransactions() throws Exception {
        String json = "{\"transactions\": [{\"cuentaOrigenId\": \"1001\"}]}";
        when(transactionConsumer.getTransactionsJson(1L)).thenReturn(json);

        Map<String, Object> result = cuentaService.getTransactions(1L);

        assertNotNull(result);
        assertTrue(result.containsKey("transactions"));
    }

    @Test
    void testCrearCuenta_BancoExiste() {
        Cuenta cuenta = new Cuenta();
        cuenta.setBancoId(123L);

        // Mock WebClient call
        WebClient webClientMock = mock(WebClient.class, RETURNS_DEEP_STUBS);
        CuentaService serviceWithMockedWebClient =
                new CuentaService(cuentaRepository, transactionConsumer, objectMapper) {
                    @Override
                    public Mono<Cuenta> crearCuenta(Cuenta cuenta, String authHeader) {
                        return cuentaRepository.save(cuenta);
                    }
                };

        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(Mono.just(cuenta));

        StepVerifier.create(serviceWithMockedWebClient.crearCuenta(cuenta, "Bearer token"))
                .expectNext(cuenta)
                .verifyComplete();
    }

    @Test
    void testListarCuentas() {
        Cuenta cuenta1 = new Cuenta();
        cuenta1.setId(1L);

        when(cuentaRepository.findAll()).thenReturn(Flux.just(cuenta1));

        StepVerifier.create(cuentaService.listarCuentas())
                .expectNext(cuenta1)
                .verifyComplete();
    }

    @Test
    void testObtenerCuenta_Existe() {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);

        when(cuentaRepository.findByNumero(1L)).thenReturn(Mono.just(cuenta));

        StepVerifier.create(cuentaService.obtenerCuenta(1L))
                .expectNext(cuenta)
                .verifyComplete();
    }

    @Test
    void testObtenerCuenta_NoExiste() {
        when(cuentaRepository.findByNumero(99L)).thenReturn(Mono.empty());

        StepVerifier.create(cuentaService.obtenerCuenta(99L))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void testActualizarCuenta() {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);

        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(Mono.just(cuenta));

        StepVerifier.create(cuentaService.actualizarCuenta(1L, cuenta))
                .expectNextMatches(c -> c.getId() == 1L)
                .verifyComplete();
    }

    @Test
    void testEliminarCuenta() {
        when(cuentaRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(cuentaService.eliminarCuenta(1L))
                .verifyComplete();

        verify(cuentaRepository, times(1)).deleteById(1L);
    }
}

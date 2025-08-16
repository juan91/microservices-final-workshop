package org.example.accountservice.controller;

import org.example.accountservice.model.Cuenta;
import org.example.accountservice.service.CuentaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@WebFluxTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CuentaService cuentaService;

    // --- POST ---
    @Test
    void testCrearCuenta() {
        Cuenta cuenta = new Cuenta(1L, "12345", "Juan",new BigDecimal(300), 1L, LocalDateTime.now());
        Mockito.when(cuentaService.crearCuenta(Mockito.any(), Mockito.anyString()))
                .thenReturn(Mono.just(cuenta));

        webTestClient.post()
                .uri("/accounts/api")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.numero").isEqualTo("12345")
                .jsonPath("$.titular").isEqualTo("Juan");
    }


    @Test
    void testListarCuentas() {
        Cuenta cuenta1 = new Cuenta(1L, "12345", "Juan",new BigDecimal(300), 1L, LocalDateTime.now());
        Cuenta cuenta2 = new Cuenta(2L, "321", "Ana",new BigDecimal(300), 1L, LocalDateTime.now());

        Mockito.when(cuentaService.listarCuentas())
                .thenReturn(Flux.just(cuenta1, cuenta2));

        webTestClient.get()
                .uri("/accounts/api")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[1].id").isEqualTo(2);
    }

    @Test
    void testObtenerCuenta() {
        Cuenta cuenta = new Cuenta(1L, "12345", "Juan",new BigDecimal(300), 1L, LocalDateTime.now());
        Mockito.when(cuentaService.obtenerCuenta(1L))
                .thenReturn(Mono.just(cuenta));

        webTestClient.get()
                .uri("/accounts/api/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.numero").isEqualTo("12345");
    }


    @Test
    void testActualizarCuenta() {
        Cuenta cuenta = new Cuenta(1L, "12345", "Juan",new BigDecimal(300), 1L, LocalDateTime.now());
        Mockito.when(cuentaService.actualizarCuenta(Mockito.eq(1L), Mockito.any()))
                .thenReturn(Mono.just(cuenta));

        webTestClient.put()
                .uri("/accounts/api/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.titular").isEqualTo("Juan");
    }

    @Test
    void testEliminarCuenta() {
        Mockito.when(cuentaService.eliminarCuenta(1L))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/accounts/api/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetTransactions() throws Exception {
        Mockito.when(cuentaService.getTransactions(12345L))
                .thenReturn(Map.of("movimientos", 5));

        webTestClient.get()
                .uri("/accounts/api/transaction/12345")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.movimientos").isEqualTo(5);
    }
}
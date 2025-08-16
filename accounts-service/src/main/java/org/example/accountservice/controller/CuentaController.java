package org.example.accountservice.controller;

import org.example.accountservice.model.Cuenta;
import org.example.accountservice.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;


@RestController
@RequestMapping("/accounts/api")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;


    @PostMapping
    public Mono<Cuenta> crearCuenta(@RequestBody Cuenta cuenta,  @RequestHeader("Authorization") String authHeader) {
        return cuentaService.crearCuenta(cuenta, authHeader);
    }

    @GetMapping
    public Flux<Cuenta> listarCuentas() {
        return cuentaService.listarCuentas();
    }

    @GetMapping("/{id}")
    public Mono<Cuenta> obtenerCuenta(@PathVariable Long id) {
        return cuentaService.obtenerCuenta(id);
    }

    @PutMapping("/{id}")
    public Mono<Cuenta> actualizarCuenta(@PathVariable Long id, @RequestBody Cuenta cuenta) {
        return cuentaService.actualizarCuenta(id, cuenta);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> eliminarCuenta(@PathVariable Long id) {
        return cuentaService.eliminarCuenta(id);
    }

    @GetMapping("/transaction/{accountNumber}")
    public Map<String, Object> getTransactions(@PathVariable Long accountNumber) throws Exception {
        return cuentaService.getTransactions(accountNumber);
    }
}
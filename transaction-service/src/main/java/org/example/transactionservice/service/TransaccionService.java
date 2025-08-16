package org.example.transactionservice.service;

import org.example.transactionservice.model.Cuenta;
import org.example.transactionservice.model.Transactions;
import org.example.transactionservice.repo.TransaccionRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransaccionService {

    private final WebClient cuentasClient = WebClient.create("http://localhost:8082");
    private final TransaccionRepository transaccionRepository;
    private final RabbitTemplate rabbitTemplate;

    public TransaccionService(TransaccionRepository transaccionRepository, RabbitTemplate rabbitTemplate) {
        this.transaccionRepository = transaccionRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Mono<Transactions> transferir(Long cuentaOrigenId, Long cuentaDestinoId, BigDecimal monto, String token) {
        // 1. Consultar cuentas para obtener bancoId
        Mono<Cuenta> origen = cuentasClient.get()
                .uri("/accounts/api/{id}", cuentaOrigenId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Cuenta.class);

        Mono<Cuenta> destino = cuentasClient.get()
                .uri("/accounts/api/{id}", cuentaDestinoId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Cuenta.class);

        return Mono.zip(origen, destino)
                .flatMap(tuple -> {
                    Cuenta cOrigen = tuple.getT1();
                    Cuenta cDestino = tuple.getT2();
                    System.out.println(cOrigen.getBancoId() + " destino  " + cDestino.getBancoId());

                    // 3. Determinar si es intra o interbancaria
                    if (cOrigen.getBancoId().equals(cDestino.getBancoId())) {
                        return procesarIntraBancaria(cOrigen, cDestino, monto);
                    } else {
                        return procesarInterBancaria(cOrigen, cDestino, monto);
                    }
                });
    }

    private Mono<Transactions> procesarIntraBancaria(Cuenta origen, Cuenta destino, BigDecimal monto) {
        // Validar fondos
        if (origen.getSaldo().compareTo(monto) < 0) {
            return Mono.error(new RuntimeException("Fondos insuficientes"));
        }
        // Retiro
        Transactions retiro = new Transactions(null, origen.getNumero(), destino.getNumero(), monto, "RETIRO",
                LocalDateTime.now(), "COMPLETADA",null);
        // Depósito
        Transactions deposito = new Transactions(null, destino.getNumero(), origen.getNumero(), monto, "DEPOSITO",
                LocalDateTime.now(), "COMPLETADA", null);

        return transaccionRepository.save(retiro)
                .then(transaccionRepository.save(deposito))
                .thenReturn(deposito);
    }

    private Mono<Transactions> procesarInterBancaria(Cuenta origen, Cuenta destino, BigDecimal monto) {
        BigDecimal impuesto = monto.multiply(BigDecimal.valueOf(0.01)); // 1% del monto
        monto = monto.add(impuesto); // monto total con impuesto
        // Validar fondos
        if (origen.getSaldo().compareTo(monto) < 0) {
            return Mono.error(new RuntimeException("Fondos insuficientes"));
        }

        Transactions retiro = new Transactions(null, origen.getNumero(), destino.getNumero(), monto.subtract(impuesto), "RETIRO", LocalDateTime.now(),
                "COMPLETADA", impuesto);
        rabbitTemplate.convertAndSend("transaction-exchange", "transaction-routing-key", retiro);
        System.out.println("Cart created event published");

        return transaccionRepository.save(retiro);
    }
}
package org.example.transactionservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.transactionservice.dto.TransferRequest;
import org.example.transactionservice.model.Transactions;
import org.example.transactionservice.service.TransaccionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transactions/api")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService transaccionService;

    @PostMapping("/transfer")
    public Mono<ResponseEntity<Transactions>> transferir(@RequestBody TransferRequest request,
                                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return transaccionService.transferir(
                        request.getCuentaOrigenId(),
                        request.getCuentaDestinoId(),
                        request.getMonto(),
                        authHeader
                )
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    System.out.println(e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(null));
                });
    }
}
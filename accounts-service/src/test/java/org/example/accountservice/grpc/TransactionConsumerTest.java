package org.example.accountservice.grpc;

import com.google.protobuf.util.JsonFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class TransactionConsumerTest {

    private TransactionServiceGrpc.TransactionServiceBlockingStub stub;
    private TransactionConsumer consumer;

    @BeforeEach
    void setUp() {
        // Mock del stub de gRPC
        stub = Mockito.mock(TransactionServiceGrpc.TransactionServiceBlockingStub.class);


        consumer = new TransactionConsumer() {
            @Override
            public String getTransactionsJson(Long transactionId) {
                try {
                    TransactionRequest request = TransactionRequest.newBuilder()
                            .setTransactionId(transactionId)
                            .build();

                    TransactionListResponse listResponse = stub.getTransaction(request);

                    return JsonFormat.printer().includingDefaultValueFields().print(listResponse);
                } catch (Exception e) {
                    throw new RuntimeException("Error llamando al servicio gRPC", e);
                }
            }
        };
    }

    @Test
    void testGetTransactionsJson() throws Exception {
        TransactionResponse transactionResponse = TransactionResponse.newBuilder()
                .setCuentaOrigenId(1001L)
                .setCuentaDestinoId(2002L)
                .setMonto("500.75")
                .setTipo("TRANSFERENCIA")
                .setFecha("2025-08-16")
                .setEstado("COMPLETADA")
                .build();

        TransactionListResponse listResponse = TransactionListResponse.newBuilder()
                .addTransactions(transactionResponse)
                .build();


        Mockito.when(stub.getTransaction(Mockito.any(TransactionRequest.class)))
                .thenReturn(listResponse);


        String jsonResult = consumer.getTransactionsJson(1L);


        assertNotNull(jsonResult);
        assertTrue(jsonResult.contains("\"cuentaOrigenId\": \"1001\""));
        assertTrue(jsonResult.contains("\"cuentaDestinoId\": \"2002\""));
        assertTrue(jsonResult.contains("\"monto\": \"500.75\""));
        assertTrue(jsonResult.contains("\"tipo\": \"TRANSFERENCIA\""));
        assertTrue(jsonResult.contains("\"estado\": \"COMPLETADA\""));
    }

    @Test
    void testGetTransactionsJson_Error() {

        Mockito.when(stub.getTransaction(Mockito.any(TransactionRequest.class)))
                .thenThrow(new RuntimeException("gRPC error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> consumer.getTransactionsJson(99L));

        assertTrue(ex.getMessage().contains("Error llamando al servicio gRPC"));
    }
}

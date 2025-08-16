package org.example.accountservice.grpc;

import com.google.protobuf.util.JsonFormat;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {
  private ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9099).usePlaintext().build();
  private TransactionServiceGrpc.TransactionServiceBlockingStub stub = TransactionServiceGrpc.newBlockingStub(channel);

/*    public Mono<List<TransactionDTO>> getTransactions(Long transactionId) {
        TransactionRequest request = TransactionRequest.newBuilder()
                .setTransactionId(transactionId)
                .build();

        // Recibir el objeto que contiene TODA la lista
        TransactionListResponse listResponse = stub.getTransaction(request);

        // Convertir cada TransactionResponse en TransactionDTO
        List<TransactionDTO> dtos = listResponse.getTransactionsList().stream()
                .map(response -> TransactionDTO.builder()
                        .setCuentaOrigenId(response.getCuentaOrigenId())
                        .setCuentaDestinoId(response.getCuentaDestinoId())
                        .setMonto(response.getMonto())
                        .setTipo(response.getTipo())
                        .setFecha(response.getFecha())
                        .setEstado(response.getEstado())
                        .build())
                .toList();

        return Mono.just(dtos);
    }*/


/*    public Mono<TransactionListResponse> getTransactions(Long transactionId) {
        TransactionRequest request = TransactionRequest.newBuilder()
                .setTransactionId(transactionId)
                .build();

        // Aquí simplemente devolvemos lo que responde el servidor gRPC
        TransactionListResponse listResponse = stub.getTransaction(request);

        return Mono.just(listResponse);
    }*/


    public String getTransactionsJson(Long transactionId) {
        try {
            TransactionRequest request = TransactionRequest.newBuilder()
                    .setTransactionId(transactionId)
                    .build();

            TransactionListResponse listResponse = stub.getTransaction(request);

            // Serializar el objeto gRPC a JSON
            return JsonFormat.printer().includingDefaultValueFields().print(listResponse);

        } catch (Exception e) {
            throw new RuntimeException("Error llamando al servicio gRPC", e);
        }
    }
}

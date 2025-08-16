package org.example.transactionservice.grpc;

import io.grpc.stub.StreamObserver;
import org.example.transactionservice.repo.TransaccionRepository;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class TransactionsProvider extends TransactionServiceGrpc.TransactionServiceImplBase{

    private final TransaccionRepository  repository;

    public TransactionsProvider(TransaccionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void getTransaction(TransactionRequest request, StreamObserver<TransactionListResponse> responseObserver) {
        repository.findByCuentaOrigenIdOrCuentaDestinoIdOrderByFechaDesc(request.getTransactionId(), request.getTransactionId())
                .map(transaction -> TransactionResponse.newBuilder()
                        .setId(transaction.getId())
                        .setCuentaOrigenId(transaction.getCuentaOrigenId())
                        .setCuentaDestinoId(transaction.getCuentaDestinoId())
                        .setMonto(transaction.getMonto().toString())
                        .setTipo(transaction.getTipo())
                        .setFecha(transaction.getFecha().toString())
                        .setEstado(transaction.getEstado())
                        .build())
                .collectList()
                .subscribe(list -> {
                    TransactionListResponse response = TransactionListResponse.newBuilder()
                            .addAllTransactions(list)
                            .build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }, responseObserver::onError);
    }
}

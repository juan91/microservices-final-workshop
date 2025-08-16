package org.example.transferservice.listener;

import org.example.transferservice.dto.Transactions;
import org.example.transferservice.repo.TransferRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransSubscriber {

    private final TransferRepository transferRepository;

    public TransSubscriber(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    @RabbitListener(queues = "transaction-queue")
  public void receiveTransfer(Transactions transactions) {
      System.out.println("Procesando transferencia: ");
      System.out.println("Cuenta Origen ID: " + transactions.getCuentaOrigenId());
      System.out.println("Cuenta Destino ID: " + transactions.getCuentaDestinoId());
      System.out.println("Monto: " + transactions.getMonto());
      System.out.println("Impuesto: " + transactions.getImpuesto());
      System.out.println("Fecha: " + LocalDateTime.now());

      this.transferRepository.save(
              org.example.transferservice.model.Transactions.builder()
                      .monto(transactions.getMonto())
                      .impuesto(transactions.getImpuesto())
                      .tipo("DEPOSITO")
                      .fecha(LocalDateTime.now())
                      .cuentaDestinoId(transactions.getCuentaDestinoId())
                      .cuentaOrigenId(transactions.getCuentaOrigenId())
                      .estado("COMPLETADA")
                      .build()
      ).subscribe();

  }

}

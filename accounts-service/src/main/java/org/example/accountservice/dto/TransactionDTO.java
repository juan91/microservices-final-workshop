package org.example.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private BigDecimal monto;
    private String tipo; // "DEPOSITO" o "RETIRO"
    private LocalDateTime fecha;
    private String estado; // "PENDIENTE", "COMPLETADA", "RECHAZADA"
}
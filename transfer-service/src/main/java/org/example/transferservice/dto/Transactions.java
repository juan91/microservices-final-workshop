package org.example.transferservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transactions {
    private Long id;
    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private BigDecimal monto;
    private BigDecimal impuesto;
    private String tipo; // "DEPOSITO" o "RETIRO"
    private LocalDateTime fecha;
    private String estado; // "PENDIENTE", "COMPLETADA", "RECHAZADA"
}
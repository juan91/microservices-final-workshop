package org.example.transferservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "bank", value = "transferes")
public class Transactions {

    @Id
    private Long id;
    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private BigDecimal monto;
    private String tipo; // "DEPOSITO" o "RETIRO"
    private LocalDateTime fecha;
    private String estado; // "PENDIENTE", "COMPLETADA", "RECHAZADA"
    private BigDecimal impuesto;
}
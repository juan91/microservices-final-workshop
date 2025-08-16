package org.example.transactionservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "bank", value = "transactions")
public class Transactions {

    @Id
    private Long id;
    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private BigDecimal monto;
    private String tipo; // "DEPOSITO" o "RETIRO"
    private LocalDateTime fecha;
    private String estado; // "PENDIENTE", "COMPLETADA", "RECHAZADA"
    @Transient
    private BigDecimal impuesto;
}
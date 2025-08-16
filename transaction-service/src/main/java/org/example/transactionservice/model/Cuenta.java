package org.example.transactionservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {
    private Long id;
    private Long numero;
    private String titular;
    private BigDecimal saldo;
    private Long bancoId;
    private LocalDateTime createdAt;
}
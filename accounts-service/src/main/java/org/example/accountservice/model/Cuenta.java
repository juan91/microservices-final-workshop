package org.example.accountservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "bank", value = "accounts")
public class Cuenta {
    @Id
    private Long id;
    private String numero;
    private String titular;
    private BigDecimal saldo;
    private Long bancoId;
    private LocalDateTime createdAt;
}
package org.example.bankservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "bank", value = "banks")
public class Bank {
    @Id
    private int id;
    private String name;
    private String description;
    private Instant createdAt;

}

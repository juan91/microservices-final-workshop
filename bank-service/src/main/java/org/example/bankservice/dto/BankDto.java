package org.example.bankservice.dto;

import lombok.*;

import java.time.Instant;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankDto {
    private int id;
    private String name;
    private String description;
    private Instant createdAt;
}
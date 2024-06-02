package com.vismutFO.RESTservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "one_time_jwt")
public class DisposableJWT {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @NotNull(message = "jwt is mandatory")
    private String jwt;

    public UUID getId() {
        return id;
    }

    public String getJwt() {
        return jwt;
    }

}


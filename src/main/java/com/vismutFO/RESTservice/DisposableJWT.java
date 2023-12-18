package com.vismutFO.RESTservice;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "one_time_jwt")
public class DisposableJWT {
    @Id
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

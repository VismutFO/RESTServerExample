package com.vismutFO.RESTservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "folders")
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID parentId;
    @NotNull(message = "ownerName is mandatory")
    private String ownerName;

    public UUID getId() {
        return id;
    }

    public UUID getParentId() {
        return parentId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public String toString() {
        return "{\"id\": \"" + id + "\", \"parentId\" : \"" + parentId + "\"}";
    }
}

package com.vismutFO.RESTservice.repositories;

import com.vismutFO.RESTservice.entities.EntryLoginPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntryLoginPasswordRepository extends JpaRepository<EntryLoginPassword, UUID> {
    Optional<EntryLoginPassword> findByName(String name);
}


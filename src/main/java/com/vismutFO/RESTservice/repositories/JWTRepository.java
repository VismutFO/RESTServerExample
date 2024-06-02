package com.vismutFO.RESTservice.repositories;

import com.vismutFO.RESTservice.entities.DisposableJWT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JWTRepository extends JpaRepository<DisposableJWT, UUID> {
    Optional<DisposableJWT> findByJwt(String jwt);
}
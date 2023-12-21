package com.vismutFO.RESTservice.repositories;

import com.vismutFO.RESTservice.entities.EntryLoginPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EntryLoginPasswordRepository extends JpaRepository<EntryLoginPassword, UUID>, JpaSpecificationExecutor<EntryLoginPassword> {
}


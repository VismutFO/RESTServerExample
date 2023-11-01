package com.vismutFO.RESTservice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PersonCollection extends JpaRepository<Person, UUID> {
}

package com.vismutFO.RESTservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface PersonCollection extends JpaRepository<Person, UUID>, PagingAndSortingRepository<Person, UUID> {
}

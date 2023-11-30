package com.vismutFO.RESTservice.controller;

import com.vismutFO.RESTservice.Person;
import com.vismutFO.RESTservice.PersonNotFoundException;
import com.vismutFO.RESTservice.PersonRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

@RequestMapping("/api/v1/persons")
@RestController
@RequiredArgsConstructor
public class PersonController {

    private PersonRepository repository;

    @PutMapping(value = "/{id}")
    public String updatePerson(@Valid @PathVariable("id") UUID id, @Valid @RequestBody Person newPerson) {
        return (repository.findById(id)
                .map(person -> {
                    person.setName(newPerson.getName());
                    person.setLogin(newPerson.getLogin());
                    person.setPassword(newPerson.getPassword());
                    person.setUrl(newPerson.getUrl());
                    return repository.save(person);
                })
                .orElseThrow(() -> new PersonNotFoundException(id))).toStringFull();
    }
}


package com.vismutFO.RESTservice;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping("/persons")
@RestController
public class PersonController {

    @Autowired
    private PersonRepository repository;

    @PostMapping
    public ResponseEntity<Person> createPerson(@Valid @RequestBody Person person) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(person));
    }

    @GetMapping
    public ResponseEntity<List<Person>> getAll() {
        return ResponseEntity.ok().body(repository.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<String> getPerson(@Valid @PathVariable("id") UUID id) {
        return ResponseEntity.ok().body((repository.findById(id).orElseThrow(() -> new PersonNotFoundException(id))).toStringFull());
    }

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

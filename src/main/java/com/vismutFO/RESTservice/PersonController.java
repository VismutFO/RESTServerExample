package com.vismutFO.RESTservice;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class PersonController {

    private final PersonCollection collection;

    public PersonController(PersonCollection collection) {
        this.collection = collection;
    }

    @PostMapping("/persons")
    public ResponseEntity<Person> createPerson(@Valid @RequestBody Person person) {
        return ResponseEntity.status(201).body(collection.save(person));
    }

    @GetMapping("/persons")
    public ResponseEntity<List<Person>> getAll() {
        return ResponseEntity.ok().body(collection.findAll());
    }

    @GetMapping("/persons/{id}")
    public ResponseEntity<String> getPerson(@Valid @PathVariable("id") UUID id) {
        return ResponseEntity.ok().body((collection.findById(id).orElseThrow(() -> new PersonNotFoundException(id))).toStringFull());
    }

    @PutMapping("/persons/{id}")
    public String updatePerson(@Valid @PathVariable("id") UUID id, @Valid @RequestBody Person newPerson) {
        return (collection.findById(id)
                .map(person -> {
                    person.setName(newPerson.getName());
                    person.setLogin(newPerson.getLogin());
                    person.setPassword(newPerson.getPassword());
                    person.setUrl(newPerson.getUrl());
                    return collection.save(person);
                })
                .orElseThrow(() -> new PersonNotFoundException(id))).toStringFull();
    }
}

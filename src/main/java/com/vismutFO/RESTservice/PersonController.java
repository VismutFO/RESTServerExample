package com.vismutFO.RESTservice;

import jakarta.validation.Valid;
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
    public Person create(@Valid @RequestBody Person person) {
        return collection.save(person);
    }

    @GetMapping("/persons")
    public List<Person> getAll() {
        return collection.findAll();
    }

    @GetMapping("/persons/{id}")
    public String getById(@Valid @PathVariable("id") UUID id) {
        return (collection.findById(id).orElseThrow(() -> new PersonNotFoundException(id))).toStringFull();
    }

    @PostMapping("/persons/{id}")
    public String set(@Valid @PathVariable("id") UUID id, @Valid @RequestBody Person newPerson) {
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

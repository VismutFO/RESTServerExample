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

    @PostMapping("/persons/{id}")
    public Person create(@Valid @RequestParam(value = "name") String name, @Valid @RequestParam(value = "login") String login,
                         @Valid @RequestParam(value = "password") String password, @Valid @RequestParam(value = "url") String url) {
        return collection.save(new Person(name, login, password, url));
    }

    @GetMapping("/persons")
    public List<Person> getAll() {
        return collection.findAll();
    }

    @GetMapping("/persons/{id}")
    public Person getById(@PathVariable("id") UUID id) {
        return collection.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
    }

    @PutMapping("/persons/{id}")
    public Person set(@PathVariable("id") UUID id,
                      @RequestParam(value = "name") String name, @RequestParam(value = "login") String login,
                         @RequestParam(value = "password") String password, @RequestParam(value = "url") String url) {
        Person person = collection.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        collection.delete(person);
        person.setName(name);
        person.setLogin(login);
        person.setPassword(password);
        person.setUrl(url);
        return collection.save(person);
    }
}

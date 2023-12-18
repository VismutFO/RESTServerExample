package com.vismutFO.RESTservice.controller;

import com.vismutFO.RESTservice.*;
import com.vismutFO.RESTservice.services.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequestMapping("/api/v1/persons")
@RestController
@RequiredArgsConstructor
public class PersonController {

    private static class PersonNameAndId {
        UUID id;
        String name;
        PersonNameAndId(UUID id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private final PersonRepository repository;

    private final JWTRepository jwtRepository;

    private final JwtService jwtService;

    @PostMapping(value = "/updatePerson")
    public ResponseEntity<String> updatePerson(@Valid @RequestHeader("Authorization") String authHeader, @Valid @RequestBody Person newPerson) {
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid authHeader");
        }
        String jwt = authHeader.substring(7);
        String userName = jwtService.extractUserName(jwt);
        return ResponseEntity.ok().body(repository.findByName(userName)
                .map(person -> {
                    person.setName(newPerson.getName());
                    person.setLogin(newPerson.getLogin());
                    person.setPassword(newPerson.getPassword());
                    person.setUrl(newPerson.getUrl());
                    return repository.save(person);
                })
                .orElseThrow(() -> new PersonNotFoundException(userName)).toStringFull());
    }

    @GetMapping(value = "/shareProfile")
    public ResponseEntity<String> shareProfile(@Valid @RequestHeader("Authorization") String authHeader) {
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid authHeader");
        }
        String jwt = authHeader.substring(7);
        String userName = jwtService.extractUserName(jwt);
        Optional<Person> user = repository.findByName(userName);
        UserDetails userDetails;
        if (user.isEmpty()) {
            throw new PersonNotFoundException(userName);
        }
        else {
            userDetails = user.get();
        }
        UUID tokenId = UUID.randomUUID();
        String disposableJwt = jwtService.generateToken(userDetails, JWTType.DISPOSABLE, tokenId);
        jwtRepository.save(new DisposableJWT(tokenId, disposableJwt));
        return ResponseEntity.ok().body(disposableJwt);
    }

    @GetMapping(value = "/allPersons")
    public ResponseEntity<List<PersonNameAndId>> getAll() {
        return ResponseEntity.ok().body(repository.findAll().stream().map(person -> new PersonNameAndId(person.getId(), person.getName()))
                .collect(Collectors.toList()));
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<String> getPerson(@Valid @RequestHeader("Authorization") String authHeader) {
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid authHeader");
        }
        String jwt = authHeader.substring(7);
        String userName = jwtService.extractUserName(jwt);
        return ResponseEntity.ok().body(repository.findByName(userName).orElseThrow(() -> new PersonNotFoundException(userName)).toStringFull());
    }
}


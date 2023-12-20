package com.vismutFO.RESTservice.controller;

import com.vismutFO.RESTservice.*;
import com.vismutFO.RESTservice.services.JwtService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = "/updatePerson")
    public ResponseEntity<String> updatePerson(@Valid @RequestHeader("Authorization") String authHeader, @Valid @RequestBody Person newPerson) {
        String jwtType = jwtService.extractClaimFromHeader(authHeader, "type");
        if (jwtType.equals("DISPOSABLE")) {
            throw new IllegalArgumentException("Cannot use updatePerson with disposable jwt");
        }
        else if (!jwtType.equals("CONSTANT")) {
            throw new IllegalArgumentException("Invalid jwt type");
        }
        String userName = jwtService.extractClaimFromHeader(authHeader, "name");
        return ResponseEntity.ok().body(repository.findByName(userName)
                .map(person -> {
                    person.setName(newPerson.getName());
                    person.setLogin(newPerson.getLogin());
                    person.setPassword(passwordEncoder.encode(newPerson.getPassword()));
                    person.setUrl(newPerson.getUrl());
                    return repository.save(person);
                })
                .orElseThrow(() -> new PersonNotFoundException(userName)).toStringFull());
    }

    @GetMapping(value = "/shareProfile")
    public ResponseEntity<String> shareProfile(@Valid @RequestHeader("Authorization") String authHeader, @Valid @RequestHeader("Expires") String expireDate) {
        String jwtType = jwtService.extractClaimFromHeader(authHeader, "type");
        if (jwtType.equals("DISPOSABLE")) {
            throw new IllegalArgumentException("Cannot use sharePerson with disposable jwt");
        }
        else if (!jwtType.equals("CONSTANT")) {
            throw new IllegalArgumentException("Invalid jwt type");
        }
        String userName = jwtService.extractClaimFromHeader(authHeader, "name");
        Optional<Person> user = repository.findByName(userName);
        UserDetails userDetails;
        if (user.isEmpty()) {
            throw new PersonNotFoundException(userName);
        }
        else {
            userDetails = user.get();
        }
        UUID tokenId = UUID.randomUUID();
        String disposableJwt;
        try {
            disposableJwt = jwtService.generateToken(userDetails, "DISPOSABLE", tokenId, new Date(Long.parseLong(expireDate)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        jwtRepository.save(new DisposableJWT(tokenId, disposableJwt));
        return ResponseEntity.ok().body(disposableJwt);
    }

    @GetMapping(value = "/allPersons")
    public ResponseEntity<List<PersonNameAndId>> getAll(@Valid @RequestHeader("Authorization") String authHeader) {
        String jwtType = jwtService.extractClaimFromHeader(authHeader, "type");
        if (jwtType.equals("DISPOSABLE")) {
            throw new IllegalArgumentException("Cannot use getAll with disposable jwt");
        }
        else if (!jwtType.equals("CONSTANT")) {
            throw new IllegalArgumentException("Invalid jwt type");
        }
        return ResponseEntity.ok().body(repository.findAll().stream().map(person -> new PersonNameAndId(person.getId(), person.getName()))
                .collect(Collectors.toList()));
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<String> getPerson(@Valid @RequestHeader("Authorization") String authHeader) {
        String jwtType = jwtService.extractClaimFromHeader(authHeader, "type");
        if (jwtType.equals("DISPOSABLE")) {
            throw new IllegalArgumentException("Cannot use getPerson with disposable jwt");
        }
        else if (!jwtType.equals("CONSTANT")) {
            throw new IllegalArgumentException("Invalid jwt type");
        }
        String userName = jwtService.extractClaimFromHeader(authHeader, "name");
        return ResponseEntity.ok().body(repository.findByName(userName).orElseThrow(() -> new PersonNotFoundException(userName)).toStringFull());
    }

    @GetMapping(value = "/getProfileByDisposable")
    @Transactional
    public ResponseEntity<String> getPersonDisposable(@Valid @RequestHeader("Authorization") String authHeader) {
        String jwtType = jwtService.extractClaimFromHeader(authHeader, "type");
        if (jwtType.equals("CONSTANT")) {
            throw new IllegalArgumentException("Cannot use getPersonDisposable with constant jwt");
        }
        else if (!jwtType.equals("DISPOSABLE")) {
            throw new IllegalArgumentException("Invalid jwt type");
        }
        String userName = jwtService.extractClaimFromHeader(authHeader, "name");
        String jwt = authHeader.substring(7);
        if (!jwtService.isTokenValid(jwt, repository.findByName(userName).orElseThrow(() -> new PersonNotFoundException(userName)))) {
            throw new IllegalArgumentException("token expired");
        }
        Optional<DisposableJWT> jwtInRep = jwtRepository.findByJwt(jwt);
        if (jwtInRep.isEmpty()) {
            throw new IllegalArgumentException("token have been used already");
        }
        jwtRepository.delete(jwtInRep.get());
        return ResponseEntity.ok().body(repository.findByName(userName).orElseThrow(() -> new PersonNotFoundException(userName)).toStringFull());
    }
}


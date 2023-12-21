package com.vismutFO.RESTservice.controller;

import com.vismutFO.RESTservice.*;
import com.vismutFO.RESTservice.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RequestMapping("/api/v1/persons")
@RestController
@RequiredArgsConstructor
public class PersonController {

    private final PersonRepository repository;

    private final JWTRepository jwtRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final MeterRegistry meterRegistry;

    private Timer.Sample timer;

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
        timer = Timer.start(meterRegistry);
        return ResponseEntity.ok().body(disposableJwt);
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
        Optional<DisposableJWT> jwtInRep = jwtRepository.findByJwt(jwt);
        if (jwtInRep.isEmpty()) {
            Counter alreadyUsedCounter = Counter.builder("alreadyUsedAttempts")
                    .tag("title", "alreadyUsedAttempts")
                    .description("a number of attempts go to someone else's profile with already used jwt")
                    .register(meterRegistry);
            alreadyUsedCounter.increment();
            throw new IllegalArgumentException("token have been used already");
        }
        jwtRepository.delete(jwtInRep.get());
        timer.stop(Timer.builder("timeForUse")
                .description("books searching timer")
                .tag("title", "timeForUse")
                .register(meterRegistry));
        return ResponseEntity.ok().body(repository.findByName(userName).orElseThrow(() -> new PersonNotFoundException(userName)).toStringFull());
    }
}


package com.vismutFO.RESTservice.controller;

import com.vismutFO.RESTservice.dao.request.EntryRequest;
import com.vismutFO.RESTservice.services.EntryLoginPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/persons")
@RestController
@RequiredArgsConstructor
public class EntryLoginPasswordController {

    private final EntryLoginPasswordService entryLoginPasswordService;

    @PostMapping(value = "/addEntry")
    public ResponseEntity<String> addEntry(@Valid @RequestHeader("Authorization") String authHeader, @Valid @RequestBody EntryRequest newEntry) {
        return entryLoginPasswordService.addEntry(authHeader, newEntry);
    }

    @PostMapping(value = "/updateEntry")
    public ResponseEntity<String> updateEntry(@Valid @RequestHeader("Authorization") String authHeader, @Valid @RequestHeader("EntryId") String entryIdRaw, @Valid @RequestBody EntryRequest newEntry) {
        return entryLoginPasswordService.updateEntry(authHeader, entryIdRaw, newEntry);
    }

    @GetMapping(value = "/getEntry")
    public ResponseEntity<String> getEntry(@Valid @RequestHeader("Authorization") String authHeader, @Valid @RequestHeader("EntryId") String entryIdRaw) {
        return entryLoginPasswordService.getEntry(authHeader, entryIdRaw);
    }

    @GetMapping(value = "/getAllEntries")
    public ResponseEntity<String> getAllEntries(@Valid @RequestHeader("Authorization") String authHeader) {
        return entryLoginPasswordService.getAllEntries(authHeader);
    }

    @GetMapping(value = "/getDisposableJWT")
    public ResponseEntity<String> getDisposableJWT(@Valid @RequestHeader("Authorization") String authHeader, @Valid @RequestHeader("Expires") String expireDate, @Valid @RequestHeader("EntryId") String entryIdRaw) {
        return entryLoginPasswordService.getDisposableJWT(authHeader, expireDate, entryIdRaw);
    }

    @GetMapping(value = "/getEntryByDisposableJWT")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<String> getEntryByDisposableJWT(@Valid @RequestHeader("Authorization") String authHeader) {
        return entryLoginPasswordService.getEntryByDisposableJWT(authHeader);
    }
}


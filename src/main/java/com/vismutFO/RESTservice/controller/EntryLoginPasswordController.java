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

    @PostMapping(value = "/addFolder")
    public ResponseEntity<String> addFolder(@Valid @RequestHeader("Authorization") String authHeader,
                                            @RequestHeader(value = "ParentFolder", required = false) String parentFolder) {
        return entryLoginPasswordService.addFolder(authHeader, parentFolder);
    }

    @PostMapping(value = "/addEntry")
    public ResponseEntity<String> addEntry(@Valid @RequestHeader("Authorization") String authHeader,
                                           @RequestHeader(value = "ParentFolder", required = false) String parentFolder, @Valid @RequestBody EntryRequest newEntry) {
        return entryLoginPasswordService.addEntry(authHeader, parentFolder, newEntry);
    }

    @PostMapping(value = "/updateEntry")
    public ResponseEntity<String> updateEntry(@Valid @RequestHeader("Authorization") String authHeader,
                                              @Valid @RequestHeader("EntryId") String entryIdRaw,
                                              @RequestHeader(value = "ParentFolder", required = false) String parentFolder,
                                              @Valid @RequestBody EntryRequest newEntry) {
        return entryLoginPasswordService.updateEntry(authHeader, entryIdRaw, parentFolder, newEntry);
    }

    @GetMapping(value = "/getEntry")
    public ResponseEntity<String> getEntry(@Valid @RequestHeader("Authorization") String authHeader,
                                           @Valid @RequestHeader("EntryId") String entryIdRaw) {
        return entryLoginPasswordService.getEntry(authHeader, entryIdRaw);
    }

    @GetMapping(value = "/getAllEntries")
    public ResponseEntity<String> getAllEntries(@Valid @RequestHeader("Authorization") String authHeader,
                                                @RequestHeader(value = "ParentFolder", required = false) String parentFolder) {
        return entryLoginPasswordService.getAllEntries(authHeader, parentFolder);
    }

    @GetMapping(value = "/getAllFolders")
    public ResponseEntity<String> getAllFolders(@Valid @RequestHeader("Authorization") String authHeader,
                                                @RequestHeader(value = "ParentFolder", required = false) String parentFolder) {
        return entryLoginPasswordService.getAllFolders(authHeader, parentFolder);
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


package com.vismutFO.RESTservice.services;

import com.vismutFO.RESTservice.dao.request.EntryRequest;
import org.springframework.http.ResponseEntity;

public interface EntryLoginPasswordService {
    public ResponseEntity<String> addEntry(String authHeader, EntryRequest newEntry);

    public ResponseEntity<String> updateEntry(String authHeader, String entryIdRaw, EntryRequest newEntry);

    public ResponseEntity<String> getEntry(String authHeader, String entryIdRaw);

    public ResponseEntity<String> getAllEntries(String authHeader);

    public ResponseEntity<String> getDisposableJWT(String authHeader, String expireDate, String entryIdRaw);

    public ResponseEntity<String> getEntryByDisposableJWT(String authHeader);
}

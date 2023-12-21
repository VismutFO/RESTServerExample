package com.vismutFO.RESTservice.services;

import com.vismutFO.RESTservice.dao.request.EntryRequest;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface EntryLoginPasswordService {

    public ResponseEntity<String> addFolder(String authHeader, String parentFolderRaw);
    public ResponseEntity<String> addEntry(String authHeader, String parentFolderRaw, EntryRequest newEntry);

    public ResponseEntity<String> updateEntry(String authHeader, String entryIdRaw, String parentFolderRaw, EntryRequest newEntry);

    public ResponseEntity<String> getEntry(String authHeader, String entryIdRaw);

    public ResponseEntity<String> getAllEntries(String authHeader, String parentFolderRaw);

    public ResponseEntity<String> getAllFolders(String authHeader, String parentFolderRaw);

    public ResponseEntity<String> getDisposableJWT(String authHeader, String expireDate, String entryIdRaw);

    public ResponseEntity<String> getEntryByDisposableJWT(String authHeader);
}

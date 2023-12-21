package com.vismutFO.RESTservice.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vismutFO.RESTservice.EntrySpecification;
import com.vismutFO.RESTservice.FolderSpecification;
import com.vismutFO.RESTservice.JWTUsedClaims;
import com.vismutFO.RESTservice.SearchCriteria;
import com.vismutFO.RESTservice.dao.request.EntryRequest;
import com.vismutFO.RESTservice.entities.DisposableJWT;
import com.vismutFO.RESTservice.entities.EntryLoginPassword;
import com.vismutFO.RESTservice.entities.Folder;
import com.vismutFO.RESTservice.exceptions.EntryLoginPasswordNotFoundException;
import com.vismutFO.RESTservice.exceptions.FolderNotFoundException;
import com.vismutFO.RESTservice.repositories.EntryLoginPasswordRepository;
import com.vismutFO.RESTservice.repositories.FolderRepository;
import com.vismutFO.RESTservice.repositories.JWTRepository;
import com.vismutFO.RESTservice.services.EntryLoginPasswordService;
import com.vismutFO.RESTservice.services.JwtService;
import com.vismutFO.RESTservice.services.UserService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntryLoginPasswordServiceImpl implements EntryLoginPasswordService {

    private static class EntryNameAndId {
        UUID id;
        String name;
        EntryNameAndId(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "{\"id\": \"" + id + "\", \"name\" : \"" + name + "\"}";
        }
    }

    private final EntryLoginPasswordRepository entryLoginPasswordRepository;

    private final FolderRepository folderRepository;

    private final UserService userService;

    private final JWTRepository jwtRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final MeterRegistry meterRegistry;

    private Timer.Sample timer;

    @Override
    public ResponseEntity<String> addFolder(String authHeader, String parentFolderRaw) {
        String jwt = jwtService.getTokenFromHeader(authHeader);
        String jwtType = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.TYPE);
        if (jwtType.equals("DISPOSABLE")) {
            throw new IllegalArgumentException("Cannot use addEntry with disposable jwt");
        }
        else if (!jwtType.equals("CONSTANT")) {
            throw new IllegalArgumentException("Invalid jwt type");
        }
        String userName = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.NAME);
        if (parentFolderRaw == null || parentFolderRaw.length() == 0) {
            Folder folder = Folder.builder().parentId(null).ownerName(userName).build();
            UUID folderId = folder.getId();
            folderRepository.save(folder);
            return ResponseEntity.ok().body(folderId.toString());
        }
        UUID parentFolderId = UUID.fromString(parentFolderRaw);
        Folder parentFolder = folderRepository.findById(parentFolderId).orElseThrow(() -> new FolderNotFoundException(parentFolderId));
        if (!userName.equals(parentFolder.getOwnerName())) {
            throw new IllegalArgumentException("Trying to access someone else's folder");
        }

        Folder folder = Folder.builder().parentId(parentFolderId).ownerName(userName).build();
        UUID folderId = folder.getId();
        folderRepository.save(folder);
        return ResponseEntity.ok().body(folderId.toString());
    }

    @Override
    public ResponseEntity<String> addEntry(String authHeader, String parentFolderRaw, EntryRequest newEntry) {
        String jwt = jwtService.getTokenFromHeader(authHeader);
        String jwtType = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.TYPE);
        if (jwtType.equals("DISPOSABLE")) {
            throw new IllegalArgumentException("Cannot use addEntry with disposable jwt");
        }
        else if (!jwtType.equals("CONSTANT")) {
            throw new IllegalArgumentException("Invalid jwt type");
        }
        if (newEntry.getEntryName().isEmpty() || newEntry.getEntryPassword().isEmpty()) {
            throw new IllegalArgumentException("entry name or entry password is null");
        }
        String userName = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.NAME);

        EntryLoginPassword entry = EntryLoginPassword.builder().name(newEntry.getEntryName())
                .login(newEntry.getLogin()).password(passwordEncoder.encode(newEntry.getEntryPassword()))
                .url(newEntry.getUrl()).ownerName(userName).build();

        if (parentFolderRaw == null || parentFolderRaw.length() == 0) {
            entryLoginPasswordRepository.save(entry);
            return ResponseEntity.status(HttpStatus.CREATED).body(entry.getId().toString());
        }

        UUID parentFolderId = UUID.fromString(parentFolderRaw);
        Folder parentFolder = folderRepository.findById(parentFolderId).orElseThrow(() -> new FolderNotFoundException(parentFolderId));
        if (!userName.equals(parentFolder.getOwnerName())) {
            throw new IllegalArgumentException("Trying to access someone else's folder");
        }
        entry.setFolderId(parentFolderId);

        entryLoginPasswordRepository.save(entry);
        return ResponseEntity.status(HttpStatus.CREATED).body(entry.getId().toString());
    }

    @Override
    public ResponseEntity<String> updateEntry(String authHeader, String entryIdRaw, String parentFolderRaw, EntryRequest newEntry) {
        String jwt = jwtService.getTokenFromHeader(authHeader);
        final String jwtType = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.TYPE);
        if (jwtType.equals("DISPOSABLE")) {
            throw new IllegalArgumentException("Cannot use updateEntry with disposable jwt");
        }
        else if (!jwtType.equals("CONSTANT")) {
            throw new IllegalArgumentException("Invalid jwt type");
        }
        final String userName = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.NAME);
        final UUID entryId = UUID.fromString(entryIdRaw);
        EntryLoginPassword entry = entryLoginPasswordRepository.findById(entryId).orElseThrow(() -> new EntryLoginPasswordNotFoundException(entryId));
        if (!userName.equals(entry.getOwnerName())) {
            throw new IllegalArgumentException("Trying to update someone else's entry");
        }
        if (parentFolderRaw == null || parentFolderRaw.length() == 0) {
            return ResponseEntity.ok().body(entryLoginPasswordRepository.findById(entryId)
                    .map(person -> {
                        person.setName(newEntry.getEntryName());
                        person.setLogin(newEntry.getLogin());
                        person.setPassword(passwordEncoder.encode(newEntry.getEntryPassword()));
                        person.setUrl(newEntry.getUrl());
                        return entryLoginPasswordRepository.save(person);
                    })
                    .orElseThrow(() -> new EntryLoginPasswordNotFoundException(entryId)).toStringFull());
        }

        UUID parentFolderId = UUID.fromString(parentFolderRaw);
        Folder parentFolder = folderRepository.findById(parentFolderId).orElseThrow(() -> new FolderNotFoundException(parentFolderId));
        if (!userName.equals(parentFolder.getOwnerName())) {
            throw new IllegalArgumentException("Trying to access someone else's folder");
        }
        entry.setFolderId(parentFolderId);

        return ResponseEntity.ok().body(entryLoginPasswordRepository.findById(entryId)
                .map(person -> {
                    person.setName(newEntry.getEntryName());
                    person.setLogin(newEntry.getLogin());
                    person.setPassword(passwordEncoder.encode(newEntry.getEntryPassword()));
                    person.setUrl(newEntry.getUrl());
                    return entryLoginPasswordRepository.save(person);
                })
                .orElseThrow(() -> new EntryLoginPasswordNotFoundException(entryId)).toStringFull());
    }

    @Override
    public ResponseEntity<String> getEntry(String authHeader, String entryIdRaw) {
        String jwt = jwtService.getTokenFromHeader(authHeader);
        final String jwtType = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.TYPE);
        if (jwtType.equals("DISPOSABLE")) {
            throw new IllegalArgumentException("Cannot use getEntry with disposable jwt");
        }
        else if (!jwtType.equals("CONSTANT")) {
            throw new IllegalArgumentException("Invalid jwt type");
        }
        final String userName = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.NAME);
        final UUID entryId = UUID.fromString(entryIdRaw);
        EntryLoginPassword entry = entryLoginPasswordRepository.findById(entryId).orElseThrow(() -> new EntryLoginPasswordNotFoundException(entryId));
        if (!userName.equals(entry.getOwnerName())) {
            throw new IllegalArgumentException("Trying to get someone else's entry");
        }
        return ResponseEntity.ok().body(entry.toStringFull());
    }

    @Override
    public ResponseEntity<String> getAllEntries(String authHeader, String parentFolderRaw) {
        String jwt = jwtService.getTokenFromHeader(authHeader);
        String jwtType = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.TYPE);
        if (jwtType.equals("DISPOSABLE")) {
            throw new IllegalArgumentException("Cannot use getDisposableJWT with disposable jwt");
        }
        else if (!jwtType.equals("CONSTANT")) {
            throw new IllegalArgumentException("Invalid jwt type");
        }
        final String userName = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.NAME);
        userService.userDetailsService().loadUserByUsername(userName); /* checking that userName is in repository,
                                                                        * otherwise it will throw an exception
                                                                        */

        EntrySpecification spec1 = new EntrySpecification(new SearchCriteria("ownerName", ":", userName));

        UUID parentFolderId = null;
        if (!(parentFolderRaw == null || parentFolderRaw.length() == 0)) {
            parentFolderId = UUID.fromString(parentFolderRaw);
            Folder parentFolder = folderRepository.findById(parentFolderId).orElseThrow(() -> new FolderNotFoundException(UUID.fromString(parentFolderRaw)));
            if (!userName.equals(parentFolder.getOwnerName())) {
                throw new IllegalArgumentException("Trying to access someone else's folder");
            }
        }

        EntrySpecification spec2 = new EntrySpecification(new SearchCriteria("folderId", ":", parentFolderId));


        List<EntryLoginPassword> results = entryLoginPasswordRepository.findAll(Specification.where(spec1).and(spec2));
        ObjectMapper objectMapper = new ObjectMapper();
        StringBuilder allEntries = new StringBuilder("[");
        for (EntryLoginPassword temp : results) {
            allEntries.append(temp.toString());
            allEntries.append(",");
        }
        allEntries.deleteCharAt(allEntries.length() - 1);
        allEntries.append("]");
        return ResponseEntity.ok().body(allEntries.toString());
    }

    @Override
    public ResponseEntity<String> getAllFolders(String authHeader, String parentFolderRaw) {
        String jwt = jwtService.getTokenFromHeader(authHeader);
        String jwtType = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.TYPE);
        if (jwtType.equals("DISPOSABLE")) {
            throw new IllegalArgumentException("Cannot use getDisposableJWT with disposable jwt");
        }
        else if (!jwtType.equals("CONSTANT")) {
            throw new IllegalArgumentException("Invalid jwt type");
        }
        final String userName = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.NAME);
        userService.userDetailsService().loadUserByUsername(userName); /* checking that userName is in repository,
         * otherwise it will throw an exception
         */

        FolderSpecification spec1 = new FolderSpecification(new SearchCriteria("ownerName", ":", userName));

        UUID parentFolderId = null;
        if (!(parentFolderRaw == null || parentFolderRaw.length() == 0)) {
            parentFolderId = UUID.fromString(parentFolderRaw);
            Folder parentFolder = folderRepository.findById(parentFolderId).orElseThrow(() -> new FolderNotFoundException(UUID.fromString(parentFolderRaw)));
            if (!userName.equals(parentFolder.getOwnerName())) {
                throw new IllegalArgumentException("Trying to access someone else's folder");
            }
        }

        FolderSpecification spec2 = new FolderSpecification(new SearchCriteria("folderId", ":", parentFolderId));


        List<Folder> results = folderRepository.findAll(Specification.where(spec1).and(spec2));
        ObjectMapper objectMapper = new ObjectMapper();
        StringBuilder allEntries = new StringBuilder("[");
        for (Folder temp : results) {
            allEntries.append(temp.toString());
            allEntries.append(",");
        }
        allEntries.deleteCharAt(allEntries.length() - 1);
        allEntries.append("]");
        return ResponseEntity.ok().body(allEntries.toString());
    }

    @Override
    public ResponseEntity<String> getDisposableJWT(String authHeader, String expireDate, String entryIdRaw) {
        String jwt = jwtService.getTokenFromHeader(authHeader);
        String jwtType = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.TYPE);
        if (jwtType.equals("DISPOSABLE")) {
            throw new IllegalArgumentException("Cannot use getDisposableJWT with disposable jwt");
        }
        else if (!jwtType.equals("CONSTANT")) {
            throw new IllegalArgumentException("Invalid jwt type");
        }

        final String userName = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.NAME);
        final UUID entryId = UUID.fromString(entryIdRaw);

        EntryLoginPassword entry = entryLoginPasswordRepository.findById(entryId).orElseThrow(() -> new EntryLoginPasswordNotFoundException(entryId));

        if (!userName.equals(entry.getOwnerName())) {
            throw new IllegalArgumentException("Trying to share someone else's entry");
        }

        UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userName);

        String disposableJwt;

        try {
            disposableJwt = jwtService.generateDisposableToken(userDetails, new Date(Long.parseLong(expireDate)), entryId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        jwtRepository.save(DisposableJWT.builder().jwt(disposableJwt).build());
        timer = Timer.start(meterRegistry);
        return ResponseEntity.ok().body(disposableJwt);
    }

    @Override
    public ResponseEntity<String> getEntryByDisposableJWT(String authHeader) {
        String jwt = jwtService.getTokenFromHeader(authHeader);

        String jwtType = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.TYPE);
        if (jwtType.equals("CONSTANT")) {
            throw new IllegalArgumentException("Cannot use getEntryByDisposableJWT with constant jwt");
        }
        else if (!jwtType.equals("DISPOSABLE")) {
            throw new IllegalArgumentException("Invalid jwt type");
        }
        String entryIdRaw = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.ENTRY_ID);
        UUID entryId = UUID.fromString(entryIdRaw);

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
        return ResponseEntity.ok().body(entryLoginPasswordRepository.findById(entryId).orElseThrow(() -> new EntryLoginPasswordNotFoundException(entryId)).toStringFull());
    }
}

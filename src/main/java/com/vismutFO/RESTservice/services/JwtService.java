package com.vismutFO.RESTservice.services;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.UUID;

public interface JwtService {
    String extractUserName(String token);

    String generateToken(UserDetails userDetails, String type, UUID id, Date expireDate);

    String extractClaimFromHeader(String header, String claim) throws IllegalArgumentException;

    boolean isTokenValid(String token, UserDetails userDetails);
}

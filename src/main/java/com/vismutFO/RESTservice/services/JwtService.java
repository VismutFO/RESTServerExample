package com.vismutFO.RESTservice.services;

import com.vismutFO.RESTservice.JWTUsedClaims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.UUID;

public interface JwtService {
    String generateConstantToken(UserDetails userDetails) throws IllegalArgumentException;

    String generateDisposableToken(UserDetails userDetails, Date expireDate, UUID entryId) throws  IllegalArgumentException;

    String extractClaimFromToken(String token, JWTUsedClaims claim);

    String getTokenFromHeader(String header) throws IllegalArgumentException;

    boolean isTokenValid(String token, UserDetails userDetails);
}

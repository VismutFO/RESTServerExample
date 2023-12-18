package com.vismutFO.RESTservice.services;

import com.vismutFO.RESTservice.JWTType;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface JwtService {
    String extractUserName(String token);

    String generateToken(UserDetails userDetails, JWTType type, UUID id);

    boolean isTokenValid(String token, UserDetails userDetails);
}

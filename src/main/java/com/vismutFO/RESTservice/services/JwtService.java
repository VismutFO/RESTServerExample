package com.vismutFO.RESTservice.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUserName(String token);

    String extractUserNameFromHeader(String header) throws IllegalArgumentException;

    String generateToken(UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);
}

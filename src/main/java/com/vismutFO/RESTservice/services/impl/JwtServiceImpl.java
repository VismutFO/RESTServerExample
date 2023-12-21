package com.vismutFO.RESTservice.services.impl;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import com.vismutFO.RESTservice.JWTUsedClaims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.vismutFO.RESTservice.services.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    @Override
    public String generateConstantToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, "CONSTANT", new Date(System.currentTimeMillis() + 1000 * 60 * 24), null);
    }

    @Override
    public String generateDisposableToken(UserDetails userDetails, Date expireDate, UUID entryId) {
        return generateToken(new HashMap<>(), userDetails, "DISPOSABLE", expireDate, entryId);
    }

    @Override
    public String extractClaimFromToken(String token, JWTUsedClaims claim) throws IllegalArgumentException {
        if (claim == JWTUsedClaims.NAME) {
            return extractClaim(token, Claims::getSubject);
        }
        return extractClaim(token, claim);
    }

    @Override
    public String getTokenFromHeader(String header) {
        if (StringUtils.isEmpty(header) || !StringUtils.startsWith(header, "Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid authHeader");
        }
        return header.substring(7);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractClaimFromToken(token, JWTUsedClaims.NAME);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String extractClaim(String token, JWTUsedClaims claim) {
        final Claims claims = extractAllClaims(token);
        return (String) claims.get(claim.name());
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, String type, Date expireDate, UUID entryId) {
        assert userDetails != null;
        assert type != null;
        assert expireDate != null;
        if (type.equals("DISPOSABLE")) {
            return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                    .claim(JWTUsedClaims.TYPE.name(), type)
                    .claim(JWTUsedClaims.ENTRY_ID.name(), entryId)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(expireDate)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
        }
        else if (type.equals("CONSTANT")) {
            return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                    .claim(JWTUsedClaims.TYPE.name(), type)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(expireDate)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
        }
        throw new IllegalArgumentException("Invalid type");

    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

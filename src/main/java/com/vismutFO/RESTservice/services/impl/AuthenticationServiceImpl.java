package com.vismutFO.RESTservice.services.impl;

import com.vismutFO.RESTservice.entities.EntryLoginPassword;
import com.vismutFO.RESTservice.repositories.EntryLoginPasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vismutFO.RESTservice.dao.request.SignInRequest;
import com.vismutFO.RESTservice.dao.request.SignUpRequest;
import com.vismutFO.RESTservice.dao.response.JwtAuthenticationResponse;
import com.vismutFO.RESTservice.services.AuthenticationService;
import com.vismutFO.RESTservice.services.JwtService;

import java.util.Date;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final EntryLoginPasswordRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Empty request");
        }
        EntryLoginPassword person = EntryLoginPassword.builder().name(request.getName()).login(request.getLogin())
                .url(request.getUrl()).password(passwordEncoder.encode(request.getPassword())).build();
        personRepository.save(person);
        String jwt = jwtService.generateToken(person, "CONSTANT", UUID.randomUUID(), new Date(System.currentTimeMillis() + 1000 * 60 * 24));
        return new JwtAuthenticationResponse(jwt);
    }

    @Override
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getName(), request.getPassword()));
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
        EntryLoginPassword person = personRepository.findByName(request.getName())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        String jwt = jwtService.generateToken(person, "CONSTANT", UUID.randomUUID(), new Date(System.currentTimeMillis() + 1000 * 60 * 24));
        return new JwtAuthenticationResponse(jwt);
    }
}

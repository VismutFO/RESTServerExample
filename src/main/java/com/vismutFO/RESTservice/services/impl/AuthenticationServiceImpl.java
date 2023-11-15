package com.vismutFO.RESTservice.services.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vismutFO.RESTservice.Person;
import com.vismutFO.RESTservice.PersonRepository;
import com.vismutFO.RESTservice.dao.request.SignInRequest;
import com.vismutFO.RESTservice.dao.request.SignUpRequest;
import com.vismutFO.RESTservice.dao.response.JwtAuthenticationResponse;
import com.vismutFO.RESTservice.services.AuthenticationService;
import com.vismutFO.RESTservice.services.JwtService;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        Person person = new Person(request);
        personRepository.save(person);
        String jwt = jwtService.generateToken(person);
        return new JwtAuthenticationResponse(jwt);
    }

    @Override
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        Person person = personRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        String jwt = jwtService.generateToken(person);
        return new JwtAuthenticationResponse(jwt);
    }
}

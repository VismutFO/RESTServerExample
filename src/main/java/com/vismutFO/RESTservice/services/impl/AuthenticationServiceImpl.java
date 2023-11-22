package com.vismutFO.RESTservice.services.impl;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        Person person = Person.builder().name(request.getName()).login(request.getLogin())
                .url(request.getUrl()).password(passwordEncoder.encode(request.getPassword())).build();
        personRepository.save(person);
        String jwt = jwtService.generateToken(person);
        return new JwtAuthenticationResponse(jwt);
    }

    @Override
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getName(), request.getPassword()));
        Person person = personRepository.findByName(request.getName())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        String jwt = jwtService.generateToken(person);
        return new JwtAuthenticationResponse(jwt);
    }
}

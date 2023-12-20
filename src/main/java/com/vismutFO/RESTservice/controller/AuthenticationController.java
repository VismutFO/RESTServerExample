package com.vismutFO.RESTservice.controller;

import com.vismutFO.RESTservice.dao.request.SignInRequest;
import com.vismutFO.RESTservice.dao.request.SignUpRequest;
import com.vismutFO.RESTservice.dao.response.JwtAuthenticationResponse;
import com.vismutFO.RESTservice.services.AuthenticationService;
import io.micrometer.common.util.StringUtils;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.ResponseEntity;



import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/signUp")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody SignUpRequest request) {
        return ResponseEntity.status(201).body(authenticationService.signUp(request));
    }

    @PostMapping("/signIn")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody SignInRequest request) {
        return ResponseEntity.status(200).body(authenticationService.signIn(request));
    }
}

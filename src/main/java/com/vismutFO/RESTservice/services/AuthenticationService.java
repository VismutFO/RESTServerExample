package com.vismutFO.RESTservice.services;

import com.vismutFO.RESTservice.dao.request.SignUpRequest;
import com.vismutFO.RESTservice.dao.request.SignInRequest;
import com.vismutFO.RESTservice.dao.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse signUp(SignUpRequest request);

    JwtAuthenticationResponse signIn(SignInRequest request);
}

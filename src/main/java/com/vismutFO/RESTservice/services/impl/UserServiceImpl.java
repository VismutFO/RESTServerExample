package com.vismutFO.RESTservice.services.impl;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vismutFO.RESTservice.PersonRepository;
import com.vismutFO.RESTservice.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PersonRepository personRepository;
    @Override
    public UserDetailsService userDetailsService() {
        return name -> personRepository.findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

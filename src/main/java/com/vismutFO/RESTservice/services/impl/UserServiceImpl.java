package com.vismutFO.RESTservice.services.impl;

import com.vismutFO.RESTservice.repositories.EntryLoginPasswordRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vismutFO.RESTservice.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final EntryLoginPasswordRepository personRepository;
    @Override
    public UserDetailsService userDetailsService() {
        return name -> personRepository.findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

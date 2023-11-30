package com.vismutFO.RESTservice;

import java.util.UUID;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(UUID id) {
        super("Couldn't find person with id: " + id);
    }
}

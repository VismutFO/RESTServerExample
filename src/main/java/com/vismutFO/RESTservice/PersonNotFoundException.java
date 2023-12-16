package com.vismutFO.RESTservice;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(String name) {
        super("Couldn't find person with name: " + name);
    }
}

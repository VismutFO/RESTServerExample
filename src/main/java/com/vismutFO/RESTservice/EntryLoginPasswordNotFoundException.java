package com.vismutFO.RESTservice;

public class EntryLoginPasswordNotFoundException extends RuntimeException {
    public EntryLoginPasswordNotFoundException(String name) {
        super("Couldn't find person with name: " + name);
    }
}

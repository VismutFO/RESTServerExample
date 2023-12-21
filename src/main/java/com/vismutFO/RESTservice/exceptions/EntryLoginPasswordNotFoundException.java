package com.vismutFO.RESTservice.exceptions;

import java.util.UUID;

public class EntryLoginPasswordNotFoundException extends IllegalArgumentException {
    public EntryLoginPasswordNotFoundException(UUID id) {
        super("Couldn't find entryLoginPassword with id: " + id);
    }
}

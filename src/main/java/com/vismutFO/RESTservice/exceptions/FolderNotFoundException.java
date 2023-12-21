package com.vismutFO.RESTservice.exceptions;

import java.util.UUID;

public class FolderNotFoundException extends IllegalArgumentException {
    public FolderNotFoundException(UUID id) {
        super("Couldn't find folder with id: " + id);
    }
}

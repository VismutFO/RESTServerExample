package com.vismutFO.RESTservice;

import java.util.UUID;

public record PersonRecord(UUID id, String name, String login, String password, String url) {
}

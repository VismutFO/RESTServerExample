package com.vismutFO.RESTservice.dao.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntryRequest {
    private String entryName;
    private String entryPassword;
    private String login;
    private String url;
}

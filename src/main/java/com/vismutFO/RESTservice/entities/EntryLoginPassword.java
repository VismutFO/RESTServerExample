package com.vismutFO.RESTservice.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vismutFO.RESTservice.CustomAuthorityDeserializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "entries")
public class EntryLoginPassword implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @NotNull(message = "Name is mandatory")
    private String name;
    private String login;
    @NotNull(message = "Password is mandatory")
    private String password;
    private String url;
    @NotNull(message = "ownerName is mandatory")
    private String ownerName;

    private UUID folderId;
    public EntryLoginPassword(String name, String login, String password, String url) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.url = url;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    @JsonDeserialize(using = CustomAuthorityDeserializer.class)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }
    public String getPassword() {
        return password;
    }
    public String getUrl() {
        return url;
    }
    public String getOwnerName() {
        return ownerName;
    }
    public UUID getFolderId() {
        return folderId;
    }
    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFolderId(UUID folderId) {
        this.folderId = folderId;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof EntryLoginPassword person))
            return false;
        return Objects.equals(this.id, person.id) && Objects.equals(this.name, person.name)
                && Objects.equals(this.login, person.login) && Objects.equals(this.password, person.password)
                && Objects.equals(this.url, person.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.login, this.password, this.url);
    }

    @Override
    public String toString() {
        return "{\"id\": \"" + id + "\", \"name\" : \"" + name + "\"}";
    }

    public String toStringFull() {
        return "{\"id\": \"" + id + "\", \"name\": \"" + name + "\", \"login\" : \"" + login + "\"," +
                " \"password\": \"" + password + "\", \"url\": \"" + url + "\"," + " \"ownerName\": \"" + ownerName + "\"}";
    }
}
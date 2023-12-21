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
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @NotNull(message = "Name is mandatory")
    private String userName;
    @NotNull(message = "Password is mandatory")
    private String userPassword;

    public User(String name, String password) {
        this.userName = name;
        this.userPassword = password;
    }


    public UUID getId() {
        return id;
    }

    @JsonDeserialize(using = CustomAuthorityDeserializer.class)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public String getUsername() {
        return userName;
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

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof User user))
            return false;
        return Objects.equals(this.id, user.id) && Objects.equals(this.userName, user.userName)
                &&  Objects.equals(this.userPassword, user.userPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.userName, this.userPassword);
    }

    @Override
    public String toString() {
        return "{\"id\": \"" + id + "\", \"name\" : \"" + userName + "\"}";
    }

    public String toStringFull() {
        return "{\"id\": \"" + id + "\", \"name\" : \"" + userName + "\", \"password\" : \"" + userPassword + "\"}";
    }
}

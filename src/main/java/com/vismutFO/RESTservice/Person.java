package com.vismutFO.RESTservice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Login is mandatory")
    private String login;
    @NotBlank(message = "Password is mandatory")
    private String password;
    @NotBlank(message = "Url is mandatory")
    private String url;

    Person (String name, String login, String password, String url) {
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

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
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

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof Person person))
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
        return "{\"name\": \"" + name + "\", \"login\" : \"" + login + "\"," +
                " \"password\": \"" + password + "\", \"url\": \"" + url + "\",}";
    }
}
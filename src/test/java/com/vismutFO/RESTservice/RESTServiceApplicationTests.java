package com.vismutFO.RESTservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vismutFO.RESTservice.dao.request.SignInRequest;
import com.vismutFO.RESTservice.dao.request.SignUpRequest;
import com.vismutFO.RESTservice.dao.response.JwtAuthenticationResponse;
import com.vismutFO.RESTservice.services.JwtService;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.CoreMatchers.*;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Map;
import java.util.Objects;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RESTServiceApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private PersonRepository collection;

	@Autowired
	private  JWTRepository jwtRepository;

	@Autowired
	private JwtService jwtService;

	@After("")
	public void resetCollection() {
		collection.deleteAll();
	}

	@Test
	public void signUp_Status201() {

		SignUpRequest request = new SignUpRequest("Michail", "login", "password", "/");

		ResponseEntity<JwtAuthenticationResponse> response = restTemplate.postForEntity("/api/v1/auth/signUp", request, JwtAuthenticationResponse.class);

		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		assertThat(response.getBody(), notNullValue());

		final String jwt = response.getBody().getToken();
		final String userName = jwtService.extractUserName(jwt);
		assertThat(userName, is("Michail"));
	}

	@Test
	public void signIn_Status200() {
		SignUpRequest signUpRequest = createTestPerson("Joe");

		SignInRequest request = new SignInRequest(signUpRequest.getName(), signUpRequest.getPassword());
		ResponseEntity<JwtAuthenticationResponse> response = restTemplate.postForEntity("/api/v1/auth/signIn", request, JwtAuthenticationResponse.class);

		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void whenUpdatePerson_thenStatus200() {
		SignUpRequest request = new SignUpRequest("Nick", "login2", "password2", "/./");
		ResponseEntity<JwtAuthenticationResponse> signUpResponse = restTemplate.postForEntity("/api/v1/auth/signUp", request, JwtAuthenticationResponse.class);
		assertThat(signUpResponse.getStatusCode(), is(HttpStatus.CREATED));
		//System.out.println("After signUp!!!!!!!!!!!!!!!!!!!!");

		Person person = new Person("Jack", "login", "password", "/");
		String jwt = Objects.requireNonNull(signUpResponse.getBody()).getToken();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + jwt);

		HttpEntity<Person> entity = new HttpEntity<>(person, headers);

		ResponseEntity<String> updateResponse = restTemplate.postForEntity("/api/v1/persons/updatePerson", entity, String.class);
		assertThat(updateResponse.getStatusCode(), is(HttpStatus.OK));
		Map<String, String> parsedResponse;
		try {
			parsedResponse = new ObjectMapper().readValue(updateResponse.getBody(), Map.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		assertThat(parsedResponse.get("id"), notNullValue());
		assertThat(parsedResponse.get("name"), is("Jack"));
		assertThat(parsedResponse.get("login"), is("login"));
		assertThat(parsedResponse.get("password"), notNullValue());
		assertThat(parsedResponse.get("url"), is("/"));
	}

	@Test
	public void whenGetPerson_thenStatus200() {
		SignUpRequest request = new SignUpRequest("Adam", "login2", "password2", "/./");
		ResponseEntity<JwtAuthenticationResponse> signUpResponse = restTemplate.postForEntity("/api/v1/auth/signUp", request, JwtAuthenticationResponse.class);
		assertThat(signUpResponse.getStatusCode(), is(HttpStatus.CREATED));
		//System.out.println("After signUp!!!!!!!!!!!!!!!!!!!!");

		String jwt = Objects.requireNonNull(signUpResponse.getBody()).getToken();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + jwt);

		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<String> updateResponse = restTemplate.exchange("/api/v1/persons/profile", HttpMethod.GET, entity, String.class);
		assertThat(updateResponse.getStatusCode(), is(HttpStatus.OK));
		Map<String, String> parsedResponse;
		try {
			parsedResponse = new ObjectMapper().readValue(updateResponse.getBody(), Map.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		assertThat(parsedResponse.get("id"), notNullValue());
		assertThat(parsedResponse.get("name"), is("Adam"));
		assertThat(parsedResponse.get("login"), is("login2"));
		assertThat(parsedResponse.get("password"), notNullValue());
		assertThat(parsedResponse.get("url"), is("/./"));
	}

	private SignUpRequest createTestPerson(String name) {
		SignUpRequest request = new SignUpRequest(name, "login", "password", "/");
		ResponseEntity<JwtAuthenticationResponse> response = restTemplate.postForEntity("/api/v1/auth/signUp", request, JwtAuthenticationResponse.class);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		return request;
	}

}

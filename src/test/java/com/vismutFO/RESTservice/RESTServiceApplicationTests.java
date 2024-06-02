package com.vismutFO.RESTservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vismutFO.RESTservice.dao.request.EntryRequest;
import com.vismutFO.RESTservice.dao.request.SignInRequest;
import com.vismutFO.RESTservice.dao.request.SignUpRequest;
import com.vismutFO.RESTservice.dao.response.JwtAuthenticationResponse;
import com.vismutFO.RESTservice.repositories.EntryLoginPasswordRepository;
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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RESTServiceApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private EntryLoginPasswordRepository collection;

	@Autowired
	private JwtService jwtService;

	@After("")
	public void resetCollection() {
		collection.deleteAll();
	}

	@Test
	public void signUp_Status201() {

		SignUpRequest request = new SignUpRequest("Michail", "password");

		ResponseEntity<JwtAuthenticationResponse> response = restTemplate.postForEntity("/api/v1/auth/signUp", request, JwtAuthenticationResponse.class);

		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		assertThat(response.getBody(), notNullValue());

		final String jwt = response.getBody().getToken();
		final String userName = jwtService.extractClaimFromToken(jwt, JWTUsedClaims.NAME);
		assertThat(userName, is("Michail"));
	}

	@Test
	public void signIn_Status200() {
		SignUpRequest signUpRequest = createTestPerson("Joe");

		SignInRequest request = new SignInRequest(signUpRequest.getUserName(), signUpRequest.getUserPassword());
		ResponseEntity<JwtAuthenticationResponse> response = restTemplate.postForEntity("/api/v1/auth/signIn", request, JwtAuthenticationResponse.class);

		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void whenAddEntry_thenStatus200() {
		SignUpRequest request = new SignUpRequest("Nick", "password2");
		ResponseEntity<JwtAuthenticationResponse> signUpResponse = restTemplate.postForEntity("/api/v1/auth/signUp", request, JwtAuthenticationResponse.class);
		assertThat(signUpResponse.getStatusCode(), is(HttpStatus.CREATED));

		EntryRequest entry = new EntryRequest("entryName", "password", "login", "/");
		String jwt = Objects.requireNonNull(signUpResponse.getBody()).getToken();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + jwt);

		HttpEntity<EntryRequest> entity = new HttpEntity<>(entry, headers);

		ResponseEntity<String> addResponse = restTemplate.postForEntity("/api/v1/persons/addEntry", entity, String.class);
		assertThat(addResponse.getStatusCode(), is(HttpStatus.CREATED));
		assertThat(addResponse.getBody(), notNullValue());
	}

	@Test
	public void whenUpdateEntry_thenStatus200() {
		SignUpRequest request = new SignUpRequest("Alex", "password2");
		ResponseEntity<JwtAuthenticationResponse> signUpResponse = restTemplate.postForEntity("/api/v1/auth/signUp", request, JwtAuthenticationResponse.class);
		assertThat(signUpResponse.getStatusCode(), is(HttpStatus.CREATED));

		EntryRequest entry = new EntryRequest("entryName", "password", "login", "/");
		String jwt = Objects.requireNonNull(signUpResponse.getBody()).getToken();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + jwt);

		HttpEntity<EntryRequest> entity = new HttpEntity<>(entry, headers);

		ResponseEntity<String> addResponse = restTemplate.postForEntity("/api/v1/persons/addEntry", entity, String.class);
		assertThat(addResponse.getStatusCode(), is(HttpStatus.CREATED));
		assertThat(addResponse.getBody(), notNullValue());

		headers.add("EntryId", addResponse.getBody());

		EntryRequest entryUpdate = new EntryRequest("entryName2", "password2", "login2", "/");
		HttpEntity<EntryRequest> entityUpdate = new HttpEntity<>(entryUpdate, headers);

		ResponseEntity<String> updateResponse = restTemplate.postForEntity("/api/v1/persons/updateEntry", entity, String.class);

		assertThat(updateResponse.getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void whenGetEntry_thenStatus200() {
		SignUpRequest request = new SignUpRequest("AnotherName", "password2");
		ResponseEntity<JwtAuthenticationResponse> signUpResponse = restTemplate.postForEntity("/api/v1/auth/signUp", request, JwtAuthenticationResponse.class);
		assertThat(signUpResponse.getStatusCode(), is(HttpStatus.CREATED));

		EntryRequest entry = new EntryRequest("entryName", "password", "login", "/");
		String jwt = Objects.requireNonNull(signUpResponse.getBody()).getToken();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + jwt);

		HttpEntity<EntryRequest> entity = new HttpEntity<>(entry, headers);

		ResponseEntity<String> addResponse = restTemplate.postForEntity("/api/v1/persons/addEntry", entity, String.class);
		assertThat(addResponse.getStatusCode(), is(HttpStatus.CREATED));
		assertThat(addResponse.getBody(), notNullValue());

		headers.add("EntryId", addResponse.getBody());

		HttpEntity<Void> entityGet = new HttpEntity<>(headers);

		ResponseEntity<String> getResponse = restTemplate.exchange("/api/v1/persons/getEntry", HttpMethod.GET, entityGet, String.class);
		assertThat(getResponse.getStatusCode(), is(HttpStatus.OK));
		System.out.println(getResponse.getBody());

		Map<String, String> parsedResponse;
		try {
			parsedResponse = new ObjectMapper().readValue(getResponse.getBody(), Map.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		assertThat(parsedResponse.get("id"), notNullValue());
		assertThat(parsedResponse.get("name"), is("entryName"));
		assertThat(parsedResponse.get("login"), is("login"));
		assertThat(parsedResponse.get("password"), notNullValue());
		assertThat(parsedResponse.get("url"), is("/"));
		assertThat(parsedResponse.get("ownerName"), is("AnotherName"));
	}

	@Test
	public void whenGetAllEntries_thenStatus200() {
		SignUpRequest request = new SignUpRequest("Artem", "password2");
		ResponseEntity<JwtAuthenticationResponse> signUpResponse = restTemplate.postForEntity("/api/v1/auth/signUp", request, JwtAuthenticationResponse.class);
		assertThat(signUpResponse.getStatusCode(), is(HttpStatus.CREATED));

		EntryRequest entry = new EntryRequest("entryName", "password", "login", "/");
		String jwt = Objects.requireNonNull(signUpResponse.getBody()).getToken();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + jwt);

		HttpEntity<EntryRequest> entity = new HttpEntity<>(entry, headers);

		ResponseEntity<String> addResponse = restTemplate.postForEntity("/api/v1/persons/addEntry", entity, String.class);
		assertThat(addResponse.getStatusCode(), is(HttpStatus.CREATED));
		assertThat(addResponse.getBody(), notNullValue());

		HttpEntity<Void> entityGet = new HttpEntity<>(headers);

		ResponseEntity<String> getResponse = restTemplate.exchange("/api/v1/persons/getAllEntries", HttpMethod.GET, entityGet, String.class);
		assertThat(getResponse.getStatusCode(), is(HttpStatus.OK));
		System.out.println(getResponse.getBody());

		List<Map<String, String>> parsedResponse;
		try {
			parsedResponse = new ObjectMapper().readValue(getResponse.getBody(), List.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		assertThat(parsedResponse.get(0).get("id"), notNullValue());
		assertThat(parsedResponse.get(0).get("name"), is("entryName"));
	}

	@Test
	public void getDisposableJWTAndUse_thenStatus200() {
		SignUpRequest request = new SignUpRequest("Adam", "password2");
		ResponseEntity<JwtAuthenticationResponse> signUpResponse = restTemplate.postForEntity("/api/v1/auth/signUp", request, JwtAuthenticationResponse.class);
		assertThat(signUpResponse.getStatusCode(), is(HttpStatus.CREATED));

		EntryRequest entry = new EntryRequest("entryName", "password", "login", "/");
		String jwt = Objects.requireNonNull(signUpResponse.getBody()).getToken();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + jwt);

		HttpEntity<EntryRequest> entity = new HttpEntity<>(entry, headers);

		ResponseEntity<String> addResponse = restTemplate.postForEntity("/api/v1/persons/addEntry", entity, String.class);
		assertThat(addResponse.getStatusCode(), is(HttpStatus.CREATED));

		headers.add("EntryId", addResponse.getBody());
		headers.add("Expires", String.valueOf(new Date(System.currentTimeMillis() + 1000 * 60 * 24).getTime()));

		HttpEntity<Void> entityGetDisposableJWT = new HttpEntity<>(headers);

		ResponseEntity<String> shareResponse = restTemplate.exchange("/api/v1/persons/getDisposableJWT", HttpMethod.GET, entityGetDisposableJWT, String.class);
		assertThat(shareResponse.getStatusCode(), is(HttpStatus.OK));

		String disposableJwt = shareResponse.getBody();

		HttpHeaders headersWithDisposableJwt = new HttpHeaders();
		headersWithDisposableJwt.add("Authorization", "Bearer " + disposableJwt);
		HttpEntity<Void> entityWithDisposableJwt = new HttpEntity<>(headersWithDisposableJwt);
		ResponseEntity<String> getResponseWithDisposableJwt = restTemplate.exchange("/api/v1/persons/getEntryByDisposableJWT", HttpMethod.GET, entityWithDisposableJwt, String.class);

		assertThat(getResponseWithDisposableJwt.getStatusCode(), is(HttpStatus.OK));

		Map<String, String> parsedResponse;
		try {
			parsedResponse = new ObjectMapper().readValue(getResponseWithDisposableJwt.getBody(), Map.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		assertThat(parsedResponse.get("id"), notNullValue());
		assertThat(parsedResponse.get("name"), is("entryName"));
		assertThat(parsedResponse.get("login"), is("login"));
		assertThat(parsedResponse.get("password"), notNullValue());
		assertThat(parsedResponse.get("url"), is("/"));

		// try second time with same token, should be forbidden
		ResponseEntity<String> getResponseWithDisposableJwtSecondTime = restTemplate.exchange("/api/v1/persons/getProfileByDisposable", HttpMethod.GET, entityWithDisposableJwt, String.class);

		assertThat(getResponseWithDisposableJwtSecondTime.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	private SignUpRequest createTestPerson(String name) {
		SignUpRequest request = new SignUpRequest(name, "password");
		ResponseEntity<JwtAuthenticationResponse> response = restTemplate.postForEntity("/api/v1/auth/signUp", request, JwtAuthenticationResponse.class);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		return request;
	}

}

package com.vismutFO.RESTservice;

import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RESTServiceApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private PersonCollection collection;

	@After("")
	public void resetCollection() {
		collection.deleteAll();
	}

	@Test
	public void whenCreatePerson_thenStatus201() {

		Person person = new Person("person1", "login1", "654321", "/");

		ResponseEntity<Person> response = restTemplate.postForEntity("/persons", person, Person.class);

		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		assertNotNull(Objects.requireNonNull(response.getBody()).getId());
		assertEquals(response.getBody().getName(), "Michail");
	}

	@Test
	public void givenPerson_whenGetPerson_thenStatus200() {

		UUID id = createTestPerson("Joe").getId();

		Person person = restTemplate.getForObject("/persons/{id}", Person.class, id);
		assertEquals(person.getName(), "Joe");
	}

	@Test
	public void whenUpdatePerson_thenStatus200() {

		UUID id = createTestPerson("Nick").getId();
		Person person = new Person("Michail", "login", "password", "/");
		HttpEntity<Person> entity = new HttpEntity<Person>(person);

		ResponseEntity<Person> response = restTemplate.exchange("/persons/{id}", HttpMethod.PUT, entity, Person.class, id);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertNotNull(Objects.requireNonNull(response.getBody()).getId());
		assertEquals(response.getBody().getName(), "Michail");
	}

	private Person createTestPerson(String name) {
		Person emp = new Person(name, "login", "password", "/");
		return collection.save(emp);
	}

}

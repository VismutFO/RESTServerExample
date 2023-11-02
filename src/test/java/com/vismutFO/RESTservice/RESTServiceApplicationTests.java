package com.vismutFO.RESTservice;

import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.*;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RESTServiceApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private PersonRepository collection;

	@After("")
	public void resetCollection() {
		collection.deleteAll();
	}

	@Test
	public void whenCreatePerson_thenStatus201() {

		Person person = new Person("Michail", "login", "password", "/");

		ResponseEntity<Person> response = restTemplate.postForEntity("/persons", person, Person.class);

		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		assertThat(response.getBody().getId(), notNullValue());
		assertThat(response.getBody().getName(), is("Michail"));
	}

	@Test
	public void givenPerson_whenGetPerson_thenStatus200() {

		UUID id = createTestPerson("Joe").getId();

		Person person = restTemplate.getForObject("/persons/{id}", Person.class, id);
		assertThat(person.getName(), is("Joe"));
	}

	@Test
	public void whenUpdatePerson_thenStatus200() {

		UUID id = createTestPerson("Nick").getId();
		Person person = new Person("Michail", "login", "password", "/");
		HttpEntity<Person> entity = new HttpEntity<Person>(person);

		ResponseEntity<Person> response = restTemplate.exchange("/persons/{id}", HttpMethod.PUT, entity, Person.class,
				id);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(response.getBody().getId(), notNullValue());
		assertThat(response.getBody().getName(), is("Michail"));
	}

	private Person createTestPerson(String name) {
		Person emp = new Person(name, "login", "password", "/");
		return collection.save(emp);
	}

}

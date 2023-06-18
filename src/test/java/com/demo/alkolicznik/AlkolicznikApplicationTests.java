package com.demo.alkolicznik;

import com.demo.alkolicznik.models.Beer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;

import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AlkolicznikApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	public void setUp() throws Exception {
		this.jdbcTemplate = new JdbcTemplate(createTestDataSource());
	}

	@Test
	public void anyoneCanAccessIndexPage() {
		ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void getBeerFromApi() {
		ResponseEntity<Beer> response = restTemplate.getForEntity("/api/beer/1", Beer.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		Beer beer = response.getBody();
		Beer expected = jdbcTemplate.queryForObject("SELECT * FROM beers WHERE beers.id = 1", mapToBeer());

		assertThat(beer).isEqualTo(expected);
	}

	@Test
	public void getCreatedBeerFromApi() {
		// Create new Beer and post it to database.
		// ID is set to null in constructor, because it will be generated.
		Beer beer = new Beer(null, "Lech");
		URI newBeerLocation = restTemplate.postForLocation("/api/beers", beer);
		Long id = beer.getId();
	//	System.out.println(newBeerLocation);

		// Fetch just-created entity from database through controller.
		ResponseEntity<Beer> getResponse = restTemplate.getForEntity(newBeerLocation, Beer.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		Beer actual = getResponse.getBody();
		assertThat(actual).isEqualTo(beer);

		// Additionally: fetch the beer directly from database.
		Beer dbBeer = jdbcTemplate.queryForObject("SELECT * FROM beers WHERE beers.id = ?", mapToBeer(), id);

		assertThat(dbBeer).isEqualTo(beer);
	}

	private RowMapper<Beer> mapToBeer() {
		return new RowMapper<Beer>() {
			@Override
			public Beer mapRow(ResultSet rs, int rowNum) throws SQLException {
				Beer beer = new Beer();
				beer.setId(rs.getLong("id"));
				beer.setName(rs.getString("name"));
				return beer;
			}
		};
	}

	private DataSource createTestDataSource() {
		return new EmbeddedDatabaseBuilder()
				.setName("beers")
				.addScript("/dbs/beer-schema.sql")
				.addScript("/dbs/beer-data.sql")
				.build();
	}
}

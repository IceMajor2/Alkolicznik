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
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;

import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/dbs/beer-schema.sql", "/dbs/beer-data.sql"})
class AlkolicznikApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        Beer beer = new Beer("Lech");
        ResponseEntity<Beer> postResponse = restTemplate.postForEntity("/api/beer", beer, Beer.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Beer savedBear = postResponse.getBody();
        URI location = postResponse.getHeaders().getLocation();

        // Fetch just-created entity from database through controller.
        ResponseEntity<Beer> getResponse = restTemplate.getForEntity(location, Beer.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Beer actual = getResponse.getBody();
        assertThat(actual).isEqualTo(savedBear);

        // Additionally: fetch the beer directly from database.
		String sql = "SELECT * FROM beers WHERE beers.id = ?";
        Beer dbBeer = jdbcTemplate.queryForObject(sql, mapToBeer(), savedBear.getId());

        assertThat(dbBeer).isEqualTo(savedBear);
    }

    @Test
    public void getNonExistingBeerShouldReturn404() {
        ResponseEntity<Beer> getResponse = restTemplate.getForEntity("/api/beer/9999", Beer.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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
}

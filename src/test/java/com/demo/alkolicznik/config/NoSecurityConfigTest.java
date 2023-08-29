package com.demo.alkolicznik.config;

import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.models.Beer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.postRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(DisabledVaadinContext.class)
@ActiveProfiles({"main", "no-security"})
public class NoSecurityConfigTest {

    private List<Beer> beers;

    @Autowired
    public NoSecurityConfigTest(List<Beer> beers) {
        this.beers = beers;
    }

    @Test
    void shouldAccessNonSecuredEndpoint() {
        var getResp = getRequest("/api/beer/1");
        String actualJson = getResp.getBody();
        // printResponse(postResp);

        String expectedJson = toJsonString(createBeerResponse(beers.get(0)));
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actualJson).isNotNull();
        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    void expectDisabledEndpointSecurity() {
        var getResp = getRequest("/api/beer");
        String actualJson = getResp.getBody();
        // printResponse(postResp);

        List<BeerResponseDTO> expected = beers.stream().map(BeerResponseDTO::new).toList();
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actualJson).isNotNull();
        List<BeerResponseDTO> actual = toModelList(actualJson, BeerResponseDTO.class);
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void expectDisabledMethodSecurity() {
        BeerRequestDTO request = createBeerRequest("Polczyn", "Jagodowe", 0.5);

        var postResp = postRequest("/api/beer", request);
        String actualJson = postResp.getBody();
        // printResponse(postResp);

        assertThat(postResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(actualJson).isNotNull();
        String expectedJson = toJsonString(createBeerResponse((beers.size() + 1), "Polczyn", "Jagodowe", 0.5));
        assertThat(actualJson).isEqualTo(expectedJson);
    }

    private void printResponse(ResponseEntity<?> response) {
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());
        for (var header : response.getHeaders().entrySet()) {
            System.out.println(header.getKey() + " ==> " + header.getValue());
        }
    }
}

package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.StoreResponseDTO;
import com.demo.alkolicznik.models.Store;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.demo.alkolicznik.TestUtils.*;
import static com.demo.alkolicznik.TestUtils.assertIsError;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class AdminApiTests {

    @Autowired
    private List<Store> stores;

    @Test
    @DisplayName("Get all stores w/ authorization")
    public void getStoresAllAuthorizedTest() {
        var getResponse = getRequestWithBasicAuth("/api/admin/store", "admin", "admin");
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        String jsonResponse = getResponse.getBody();
        List<StoreResponseDTO> actual = toModelList(jsonResponse, StoreResponseDTO.class);

        List<StoreResponseDTO> expected = stores.stream()
                .map(StoreResponseDTO::new)
                .toList();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Get all stores w/o authorization")
    public void getStoresAllUnauthorizedTest() {
        var getResponse = getRequest("/api/admin/store");

        String json = getResponse.getBody();

        assertIsError(json, HttpStatus.NOT_FOUND, "Resource not found", "/api/admin/store");
    }
}

package com.demo.alkolicznik;

import com.demo.alkolicznik.dto.store.StoreRequestDTO;
import com.demo.alkolicznik.dto.store.StoreUpdateDTO;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class Parameters {

    public static Stream<StoreRequestDTO> validAndInvalidStorePostRequests() {
        return Stream.of(
                new StoreRequestDTO("Dino", "Stegna", "ul. Morska 10"),
                new StoreRequestDTO("", null, "al. Wojska Polskiego 121")
        );
    }

    public static Stream<Arguments> validAndInvalidStorePutRequests() {
        return Stream.of(
                Arguments.of(5L, new StoreRequestDTO("Spolem", "Kielce", "ul. Polna 1")),
                Arguments.of(-9L, new StoreRequestDTO("ABC", "Ilawa", "ul. Jeziorna 12")),
                Arguments.of(120L, new StoreRequestDTO(null, "Krakow", "")),
                Arguments.of(1L, new StoreRequestDTO(null, null, null))
        );
    }

    public static Stream<Arguments> validAndInvalidStorePatchRequests() {
        return Stream.of(
                Arguments.of(2L, new StoreUpdateDTO("Groszek", null, null)),
                Arguments.of(-210L, new StoreUpdateDTO(null, "Bydgoszcz", "ul. Dywizjonu 303")),
                Arguments.of(250L, new StoreUpdateDTO(null, null, null))
        );
    }

    public static Stream<StoreRequestDTO> validAndInvalidStoreDeleteRequests() {
        return Stream.of(
                new StoreRequestDTO("Carrefour", "Olsztyn", "ul. Barcza 4"),
                new StoreRequestDTO(null, "Sopot", "")
        );
    }
}

package com.demo.alkolicznik.dto.city;

import lombok.*;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityDTO {

    private String city;

    public static List<CityDTO> asList(Collection<String> storeNames) {
        return storeNames.stream()
                .map(CityDTO::new)
                .toList();
    }
}

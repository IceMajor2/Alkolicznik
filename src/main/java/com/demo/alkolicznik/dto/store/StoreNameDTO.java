package com.demo.alkolicznik.dto.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StoreNameDTO {

    @JsonProperty("name")
    private String storeName;

    public static List<StoreNameDTO> asList(Collection<String> storeNames) {
        return storeNames.stream()
                .map(StoreNameDTO::new)
                .toList();
    }
}

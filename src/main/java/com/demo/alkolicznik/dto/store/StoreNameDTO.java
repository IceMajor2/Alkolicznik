package com.demo.alkolicznik.dto.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreNameDTO {

    @JsonProperty("name")
    private String storeName;

    public static List<StoreNameDTO> asList(Collection<String> storeNames) {
        return storeNames.stream()
                .map(StoreNameDTO::new)
                .toList();
    }
}

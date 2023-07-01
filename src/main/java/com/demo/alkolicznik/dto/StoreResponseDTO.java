package com.demo.alkolicznik.dto;

import com.demo.alkolicznik.models.Store;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonPropertyOrder({"id", "name", "city", "street"})
@NoArgsConstructor
@Getter
@Setter
public class StoreResponseDTO {

    private Long id;
    private String name;
    private String city;
    private String street;

    public StoreResponseDTO(Store store) {
        this.id = store.getId();
        this.name = store.getName();
        this.city = store.getCity();
        this.street = store.getStreet();
    }

    @Override
    public String toString() {
        return "ID %d. %s, %s (%s)".formatted(this.id, this.name, this.street, this.city);
    }
}

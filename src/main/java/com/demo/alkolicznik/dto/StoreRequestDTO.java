package com.demo.alkolicznik.dto;

import com.demo.alkolicznik.models.Store;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class StoreRequestDTO {

    @NotBlank(message = "Name was not specified")
    private String name;

    @NotBlank(message = "City was not specified")
    private String city;

    @NotBlank(message = "Street was not specified")
    private String street;

    public Store convertToModel() {
        Store store = new Store();
        store.setName(this.name);
        store.setCity(this.city);
        store.setStreet(this.street);
        return store;
    }
}

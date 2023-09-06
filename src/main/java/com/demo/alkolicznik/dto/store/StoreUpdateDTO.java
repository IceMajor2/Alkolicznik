package com.demo.alkolicznik.dto.store;

import com.demo.alkolicznik.dto.UpdateModel;
import com.demo.alkolicznik.exceptions.annotations.NotBlankIfExists;
import com.demo.alkolicznik.models.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
public class StoreUpdateDTO implements UpdateModel<Store> {
    
    @NotBlankIfExists(message = "Name was not specified")
    private String name;

    @NotBlankIfExists(message = "City was not specified")
    private String city;

    @NotBlankIfExists(message = "Street was not specified")
    private String street;

    public StoreUpdateDTO(StoreRequestDTO store) {
        this.name = store.getName();
        this.city = store.getCity();
        this.street = store.getStreet();
    }

    public boolean propertiesMissing() {
        return name == null && city == null && street == null;
    }

    @Override
    public boolean anythingToUpdate(Store store) {
        String currName = store.getName();
        String currCity = store.getCity();
        String currStreet = store.getStreet();

        String upName = this.getName();
        String upCity = this.getCity();
        String upStreet = this.getStreet();
        if(upName != null && !Objects.equals(currName, upName)) {
            return true;
        }
        if(upCity != null && !Objects.equals(currCity, upCity)) {
            return true;
        }
        if(upStreet != null && !Objects.equals(currStreet, upStreet)) {
            return true;
        }
        return false;
    }
}

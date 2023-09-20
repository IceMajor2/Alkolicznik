package com.demo.alkolicznik.dto.store;

import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.StoreImage;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StoreRequestDTO {

    @NotBlank(message = "Name was not specified")
    private String name;

    @NotBlank(message = "City was not specified")
    private String city;

    @NotBlank(message = "Street was not specified")
    private String street;

    public StoreRequestDTO(StoreResponseDTO store) {
        this.name = store.getName();
        this.city = store.getCity();
        this.street = store.getStreet();
    }

    public static Store toModel(StoreRequestDTO requestDTO, Optional<StoreImage> optImage) {
        Store store = new Store();
        store.setName(requestDTO.getName());
        store.setCity(requestDTO.getCity());
        store.setStreet(requestDTO.getStreet());
        optImage.ifPresent(store::setImage);
        return store;
    }
}

package com.demo.alkolicznik.dto.store;

import com.demo.alkolicznik.dto.image.StoreImageResponseDTO;
import com.demo.alkolicznik.models.Store;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.Collection;
import java.util.List;

@JsonPropertyOrder({"id", "name", "city", "street", "image"})
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StoreResponseDTO {

    private Long id;
    private String name;
    private String city;
    private String street;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private StoreImageResponseDTO image;

    public StoreResponseDTO(Store store) {
        this.id = store.getId();
        this.name = store.getName();
        this.city = store.getCity();
        this.street = store.getStreet();
        store.getImage().ifPresent(storeImage -> this.image = new StoreImageResponseDTO(storeImage));
    }

    public static List<StoreResponseDTO> asList(Collection<Store> stores) {
        return stores.stream()
                .map(StoreResponseDTO::new)
                .toList();
    }
}

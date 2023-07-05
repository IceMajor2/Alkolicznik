package com.demo.alkolicznik.dto.delete;

import com.demo.alkolicznik.models.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StoreDeleteResponseDTO {

    private Long id;
    private String name;
    private String city;
    private String street;
    private String status = "Store was deleted successfully!";

    public StoreDeleteResponseDTO(Store store) {
        this.id = store.getId();
        this.name = store.getName();
        this.city = store.getCity();
        this.street = store.getStreet();
    }

    public StoreDeleteResponseDTO(Store store, String status) {
        this.id = store.getId();
        this.name = store.getName();
        this.city = store.getCity();
        this.street = store.getStreet();
        this.status = status;
    }
}

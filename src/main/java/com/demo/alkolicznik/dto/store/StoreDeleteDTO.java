package com.demo.alkolicznik.dto.store;

import com.demo.alkolicznik.models.Store;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class StoreDeleteDTO {

    private Long id;
    private String name;
    private String city;
    private String street;
    @ToString.Exclude
    private String status = "Store was deleted successfully!";

    public StoreDeleteDTO(Store store) {
        this.id = store.getId();
        this.name = store.getName();
        this.city = store.getCity();
        this.street = store.getStreet();
    }

    public StoreDeleteDTO(Store store, String status) {
        this.id = store.getId();
        this.name = store.getName();
        this.city = store.getCity();
        this.street = store.getStreet();
        this.status = status;
    }
}

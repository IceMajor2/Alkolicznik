package com.demo.alkolicznik.dto.store;

import com.demo.alkolicznik.dto.image.ImageModelResponseDTO;
import com.demo.alkolicznik.models.Store;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@JsonPropertyOrder({"id", "name", "city", "street", "image"})
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class StoreResponseDTO {

    private Long id;
    private String name;
    private String city;
    private String street;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ImageModelResponseDTO image;

    public StoreResponseDTO(Store store) {
        this.id = store.getId();
        this.name = store.getName();
        this.city = store.getCity();
        this.street = store.getStreet();
		if(store.getImage().isPresent()) {
			this.image = new ImageModelResponseDTO(store.getImage().get());
		}
    }

    @Override
    public String toString() {
        return "ID %d. %s, %s (%s)".formatted(this.id, this.name, this.street, this.city);
    }
}

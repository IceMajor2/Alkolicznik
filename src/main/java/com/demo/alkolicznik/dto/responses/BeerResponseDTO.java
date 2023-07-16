package com.demo.alkolicznik.dto.responses;

import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.NoSuchElementException;

@JsonPropertyOrder({"id", "brand", "type", "volume"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class BeerResponseDTO {

    private Long id;
    private String brand;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String type;
    private Double volume;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ImageModelResponseDTO image;

    public BeerResponseDTO(Beer beer) {
        this.id = beer.getId();
        this.brand = beer.getBrand();
        this.type = beer.getType();
        this.volume = beer.getVolume();
        try {
            this.image = new ImageModelResponseDTO(beer.getImage().get());
        } catch (NoSuchElementException e) {
        }
    }

    @JsonIgnore
    public String getFullName() {
        StringBuilder sb = new StringBuilder(this.brand);
        if (this.type != null) {
            sb.append(" ");
            sb.append(this.type);
        }
        return sb.toString();
    }
}

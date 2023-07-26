package com.demo.alkolicznik.dto.beer;

import com.demo.alkolicznik.dto.image.ImageModelResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonPropertyOrder({"id", "brand", "type", "volume", "image"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class BeerResponseDTO extends BeerMain {

    private Long id;
    private String brand;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private String type;
//    private Double volume;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ImageModelResponseDTO image;

    public BeerResponseDTO(Beer beer) {
        this.id = beer.getId();
        this.brand = beer.getBrand();
        super.type = beer.getType();
		super.volume = beer.getVolume();
        if(beer.getImage().isPresent()) {
            this.image = new ImageModelResponseDTO(beer.getImage().get());
        }
    }

    @JsonIgnore
    public String getFullName() {
        StringBuilder sb = new StringBuilder(this.brand);
        if (this.type != null) {
            sb.append(" ").append(this.type);
        }
        return sb.toString();
    }
}

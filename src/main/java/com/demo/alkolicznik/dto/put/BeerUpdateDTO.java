package com.demo.alkolicznik.dto.put;

import com.demo.alkolicznik.exceptions.annotations.NotBlankIfExists;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class BeerUpdateDTO {

    @NotBlankIfExists(message = "Brand was not specified")
    private String brand;
    @NotBlankIfExists(message = "Type was not specified")
    private String type;
    @Positive(message = "Volume must be a positive number")
    private Double volume;

    public boolean propertiesMissing() {
        return brand == null && type == null && volume == null;
    }
}

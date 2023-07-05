package com.demo.alkolicznik.dto.put;

import com.demo.alkolicznik.exceptions.annotations.NotBlankIfExists;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class StoreUpdateDTO implements UpdateModel {
    
    @NotBlankIfExists(message = "Name was not specified")
    private String name;

    @NotBlankIfExists(message = "City was not specified")
    private String city;

    @NotBlankIfExists(message = "Street was not specified")
    private String street;

    public boolean propertiesMissing() {
        return name == null && city == null && street == null;
    }

}

package com.demo.alkolicznik.dto.store;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class StoreRequestDTO {

    @NotBlank(message = "Name was not specified")
    private String name;

    @NotBlank(message = "City was not specified")
    private String city;

    @NotBlank(message = "Street was not specified")
    private String street;

//	@JsonProperty("image_path")
//	private String imagePath;

}

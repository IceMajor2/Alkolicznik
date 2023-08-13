package com.demo.alkolicznik.dto.beer;

import java.util.Objects;

import com.demo.alkolicznik.dto.UpdateModel;
import com.demo.alkolicznik.exceptions.annotations.NotBlankIfExists;
import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonPropertyOrder({ "brand", "type", "volume", "image_path" })
public class BeerUpdateDTO implements UpdateModel<Beer> {

	@NotBlankIfExists(message = "Brand was not specified")
	private String brand;

	private String type;

	@Positive(message = "Volume must be a positive number")
	private Double volume;

	public BeerUpdateDTO(BeerRequestDTO requestDTO) {
		this.brand = requestDTO.getBrand();
		this.type = requestDTO.getType();
		this.volume = requestDTO.getVolume();
	}

	@Override
	public boolean propertiesMissing() {
		return brand == null && type == null && volume == null; //&& imagePath == null;
	}

	@Override
	public boolean anythingToUpdate(Beer beer) {
		String currBrand = beer.getBrand();
		String currType = beer.getType();
		Double currVolume = beer.getVolume();

		String upBrand = this.getBrand();
		String upType = this.getType();
		Double upVolume = this.getVolume();

		if (upBrand != null && !Objects.equals(currBrand, upBrand)) {
			return true;
		}
		if (upVolume != null && !Objects.equals(currVolume, upVolume)) {
			return true;
		}
		if (upType != null && !Objects.equals(currType, upType)) {
			if ("".equals(upType.trim()) && currType == null) {
				return false;
			}
			return true;
		}
		return false;
	}

	@JsonIgnore
	public String getFullName() {
		if (this.brand == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder(this.brand);
		if (this.type != null) {
			sb.append(" ").append(this.type);
		}
		return sb.toString();
	}
}

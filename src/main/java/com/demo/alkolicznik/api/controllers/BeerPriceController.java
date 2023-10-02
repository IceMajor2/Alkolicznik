package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerPriceService;
import com.demo.alkolicznik.dto.beerprice.BeerPriceDeleteDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
@Tag(name = "Beer Price")
public class BeerPriceController {

    private BeerPriceService beerPriceService;

    public BeerPriceController(BeerPriceService beerPriceService) {
        this.beerPriceService = beerPriceService;
    }

    @Operation(summary = "Get beer price | Get a list of beer prices",
            description = "<u>EVERYONE:</u> Get exact beer price: " +
                    "<b>/api/beer-price?store_id={store_id}&beer_id={beer_id}</b><br><br>" +
                    "<u>EVERYONE:</u> Get list of all beer prices in a given city: " +
                    "<b>/api/beer-price?city={some_city}</b><br><br>" +
                    "<u>ACCOUNTANTS:</u> Get list of all beer prices in the database: " +
                    "<b>/api/beer-price</b>",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Price(s) retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "&bull; Unauthorized (dummy response)<br>" +
                                    "&bull; Beer not found<br>" +
                                    "&bull; Store not found<br>" +
                                    "&bull; City not found",
                            content = @Content
                    )
            }
    )
    @GetMapping(value = "/beer-price", params = {"store_id", "beer_id"})
    public BeerPriceResponseDTO get(
            @RequestParam(value = "store_id", required = false) Long storeId,
            @RequestParam(value = "beer_id", required = false) Long beerId) {
        return beerPriceService.get(storeId, beerId);
    }

    @GetMapping("/beer-price")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public List<BeerPriceResponseDTO> getAll() {
        return beerPriceService.getAll();
    }

    @GetMapping(value = "/beer-price", params = "city")
    public List<BeerPriceResponseDTO> getAllByCity(
            @RequestParam(value = "city", required = false) String city) {
        return beerPriceService.getAllByCity(city);
    }

    @Operation(
            summary = "Get a list of beer prices from a store",
            description = "Getting prices of your vis-à-vis "
                    + "competition was never as easy.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Prices retrieved",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = BeerPriceResponseDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "&bull; Unauthorized (dummy response)<br>" +
                                    "&bull; Store not found",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @GetMapping("/store/{store_id}/beer-price")
    public List<BeerPriceResponseDTO> getAllByStoreId(@PathVariable("store_id") Long storeId) {
        return beerPriceService.getAllByStoreId(storeId);
    }

    @Operation(
            summary = "Get beer-specific prices",
            description = "Acquire a list of prices of your favourite beer from all stores. " +
                    "(Sorted by city, then price ascending and finally by store id).<br><br>" +
                    "<u>EVERYONE:</u> Get a list of all beer prices of a given beer: " +
                    "<b>/api/beer/{beer_id}/beer-price</b><br><br>" +
                    "<u>EVERYONE:</u> Get a list of all beer prices of a given beer in a given city: " +
                    "<b>/api/beer/{beer_id}/beer-price?city={some_city}</b>",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Prices retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "&bull; Unauthorized (dummy response)<br>" +
                                    "&bull; Beer not found<br>" +
                                    "&bull; City not found<br>",
                            content = @Content
                    )
            }
    )
    @GetMapping("/beer/{beer_id}/beer-price")
    public List<BeerPriceResponseDTO> getAllByBeerId(@PathVariable("beer_id") Long beerId) {
        return beerPriceService.getAllByBeerId(beerId);
    }

    @GetMapping(value = "/beer/{beer_id}/beer-price", params = "city")
    public List<BeerPriceResponseDTO> getAllByBeerIdAndCity(@PathVariable("beer_id") Long beerId,
                                                            @RequestParam(value = "city", required = false) String city) {
        return beerPriceService.getAllByBeerIdAndCity(beerId, city);
    }

    @Operation(
            summary = "Add price (by object or in-query parameters)",
            description = "This whole application is about prices! What are you waiting for?<br><br>" +
                    "<u>ACCOUNTANTS:</u> add beer-price by object (request body required): " +
                    "<b>/api/store/{store_id}/beer-price</b><br><br>" +
                    "<u>ACCOUNTANTS:</u> add beer-price by parameters only: " +
                    "<b>/api/store/{store_id}/beer-price?beer_id={beer_id}&beer_price={cost}</b><br><br>" +
                    "<b>CONSTRAINTS:</b><br>" +
                    "&bull; beer price must not be left empty and must be a positive number<br>" +
                    "&bull; adding by object: beer name is a combination of beer brand & type + must not be left empty<br>" +
                    "&bull; adding by object: beer volume must not be negative",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"beer_name\":\"Heineken Premium\",\"beer_volume\":0.5,\"beer_price\":4.09}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Price successfully added",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = BeerPriceResponseDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation failed",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "&bull; Unauthorized (dummy response)<br>" +
                                    "&bull; Beer not found<br>" +
                                    "&bull; Store not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Price already exists",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )

    )
    @PostMapping("/store/{store_id}/beer-price")
    public ResponseEntity<BeerPriceResponseDTO> addByObject(
            @PathVariable("store_id") Long storeId,
            @RequestBody(required = false) @Valid BeerPriceRequestDTO beerPriceRequestDTO) {
        BeerPriceResponseDTO beerPrice = beerPriceService.addByObject(storeId, beerPriceRequestDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(beerPrice.getBeer().getId())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(beerPrice);
    }

    @PostMapping(value = "/store/{store_id}/beer-price", params = {"beer_id", "beer_price"})
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<BeerPriceResponseDTO> addByParam(
            @PathVariable("store_id") Long storeId,
            @RequestParam(value = "beer_id", required = false) Long beerId,
            @RequestParam(value = "beer_price", required = false)
            @Positive(message = "Price must be a positive number") Double price) {
        BeerPriceResponseDTO beerPrice = beerPriceService.addByParam(storeId, beerId, price);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(beerPrice.getBeer().getId())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(beerPrice);
    }

    @Operation(
            summary = "Update price",
            description = "You've just put a discount on a beer? That's great!<br>"
                    + "Or maybe, please don't, prices are going up?! "
                    + "Ah well, ain't inflation a bitch.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "&bull; Price successfully updated<br>" +
                                    "&bull; Replacement is the same as original entity: nothing changes",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = BeerPriceResponseDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation failed",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "&bull; Unauthorized (dummy response)<br>" +
                                    "&bull; Price not found<br>" +
                                    "&bull; Beer not found<br>" +
                                    "&bull; Store not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Price already existed",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @PatchMapping(value = "/beer-price", params = {"store_id", "beer_id", "price"})
    public BeerPriceResponseDTO update(@RequestParam("store_id") Long storeId,
                                       @RequestParam("beer_id") Long beerId,
                                       @RequestParam("price") @Positive(message = "Price must be a positive number") Double price) {
        return beerPriceService.update(storeId, beerId, price);
    }

    @Operation(
            summary = "Delete price",
            description = "In case of you not selling a beer not anymore, "
                    + "delete it from our application. Customers will be glad!",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Price successfully deleted",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = BeerPriceDeleteDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "&bull; Unauthorized (dummy response)<br>" +
                                    "&bull; Price not found<br>" +
                                    "&bull; Beer not found<br>" +
                                    "&bull; Store not found",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @DeleteMapping("/beer-price")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public BeerPriceDeleteDTO delete(@RequestParam("store_id") Long storeId,
                                     @RequestParam("beer_id") Long beerId) {
        return beerPriceService.delete(storeId, beerId);
    }
}

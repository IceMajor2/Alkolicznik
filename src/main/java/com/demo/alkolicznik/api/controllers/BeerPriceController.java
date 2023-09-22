package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerPriceService;
import com.demo.alkolicznik.dto.beerprice.BeerPriceDeleteDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
                    "<b>/api/beer-price?store_id=${store_id}&beer_id=${beer_id}</b><br><br>" +
                    "<u>EVERYONE:</u> Get list of all beer prices in a given city: " +
                    "<b>/api/beer-price?city=${some_city}</b><br><br>" +
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
                                    "&bull; Both store & beer not found<br>" +
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

    @GetMapping("/store/{store_id}/beer-price")
    @Operation(summary = "Get prices of some store",
            description = "Getting prices of your vis-a-vis "
                    + "competition was never as easy.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "prices retrieved"),
            @ApiResponse(responseCode = "404", description = "resource not found - dummy response "
                    + "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
            @ApiResponse(responseCode = "404 (2)", description = "store not found", content = @Content)
    })
    public List<BeerPriceResponseDTO> getAllByStoreId(@PathVariable("store_id") Long storeId) {
        return beerPriceService.getAllByStoreId(storeId);
    }

    @GetMapping("/beer/{beer_id}/beer-price")
    @Operation(summary = "Get beer-specific prices",
            description = "Acquire a list of prices of your favourite beer.<br>"
                    + "<b>Options available:</b><br>"
                    + "<i>/api/beer/{beer_id}/beer-price</i> - list all beer-specific prices<br>"
                    + "<i>/api/beer/{beer_id}/beer-price?city=</i> - list all beer-specific prices in a city")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "prices retrieved"),
            @ApiResponse(responseCode = "404", description = "resource not found - dummy response "
                    + "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
            @ApiResponse(responseCode = "404 (2)", description = "beer not found", content = @Content),
            @ApiResponse(responseCode = "404 (3)", description = "city not found", content = @Content),
            @ApiResponse(responseCode = "404 (4)", description = "both beer and city not found", content = @Content)
    })
    public List<BeerPriceResponseDTO> getAllByBeerId(@PathVariable("beer_id") Long beerId) {
        return beerPriceService.getAllByBeerId(beerId);
    }

    @GetMapping(value = "/beer/{beer_id}/beer-price", params = "city")
    public List<BeerPriceResponseDTO> getAllByBeerIdAndCity(@PathVariable("beer_id") Long beerId,
                                                            @RequestParam(value = "city", required = false) String city) {
        return beerPriceService.getAllByBeerIdAndCity(beerId, city);
    }

    @PostMapping("/store/{store_id}/beer-price")
    @Operation(summary = "Add price (by properties or JSON string)",
            description = "This whole application is about prices! "
                    + "What are you waiting for?<br>"
                    + "<b>Options available:</b><br>"
                    + "<i>/api/store/{store_id}/beer-price</i> - addBeerImage by JSON string (see body below)<br>"
                    + "<i>/api/store/{store_id}/beer-price?beer_id=?beer_price=</i> - addBeerImage by parameters")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "price added"),
            @ApiResponse(responseCode = "400", description = "constraint validation failed", content = @Content),
            @ApiResponse(responseCode = "404", description = "resource not found - dummy response "
                    + "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
            @ApiResponse(responseCode = "404 (2)", description = "beer not found", content = @Content),
            @ApiResponse(responseCode = "404 (3)", description = "city not found", content = @Content),
            @ApiResponse(responseCode = "404 (4)", description = "both store and beer not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "price already exists", content = @Content)
    })
    @SecurityRequirement(name = "JWT Authentication")
    public ResponseEntity<BeerPriceResponseDTO> addByObject(
            @PathVariable("store_id") Long storeId,
            @RequestBody @Valid BeerPriceRequestDTO beerPriceRequestDTO) {
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
    @SecurityRequirement(name = "JWT Authentication")
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

    @PatchMapping(value = "/beer-price", params = {"store_id", "beer_id", "price"})
    @Operation(summary = "Update price",
            description = "You've just put a discount on a beer? That's great!<br>"
                    + "Or maybe, please don't, prices are going up?! "
                    + "Ah well, ain't inflation a bitch.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "price updated"),
            @ApiResponse(responseCode = "200 (2)", description = "price did not differ", content = @Content),
            @ApiResponse(responseCode = "400", description = "constraint validation failed", content = @Content),
            @ApiResponse(responseCode = "404", description = "resource not found - dummy response "
                    + "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
            @ApiResponse(responseCode = "404 (2)", description = "price not found", content = @Content),
            @ApiResponse(responseCode = "404 (3)", description = "beer not found", content = @Content),
            @ApiResponse(responseCode = "404 (4)", description = "store not found", content = @Content),
            @ApiResponse(responseCode = "404 (5)", description = "both store and beer not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "price already exists", content = @Content)
    })
    @SecurityRequirement(name = "JWT Authentication")
    public BeerPriceResponseDTO update(@RequestParam("store_id") Long storeId,
                                       @RequestParam("beer_id") Long beerId,
                                       @RequestParam("price") @Positive(message = "Price must be a positive number") Double price) {
        return beerPriceService.update(storeId, beerId, price);
    }

    @DeleteMapping("/beer-price")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @Operation(summary = "Delete price",
            description = "In case of you not selling a beer not anymore, "
                    + "delete it from our application. Customers will be glad!")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "price deleted"),
            @ApiResponse(responseCode = "404", description = "resource not found - dummy response "
                    + "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
            @ApiResponse(responseCode = "404 (2)", description = "price not found", content = @Content),
            @ApiResponse(responseCode = "404 (3)", description = "beer not found", content = @Content),
            @ApiResponse(responseCode = "404 (4)", description = "store not found", content = @Content),
            @ApiResponse(responseCode = "404 (5)", description = "both store and beer not found", content = @Content)
    })
    @SecurityRequirement(name = "JWT Authentication")
    public BeerPriceDeleteDTO delete(@RequestParam("store_id") Long storeId,
                                     @RequestParam("beer_id") Long beerId) {
        return beerPriceService.delete(storeId, beerId);
    }
}

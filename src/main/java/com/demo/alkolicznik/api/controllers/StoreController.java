package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.StoreService;
import com.demo.alkolicznik.dto.store.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/store")
@Tag(name = "Store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @Operation(
            summary = "Get store details",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Store details retrieved",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = StoreResponseDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Store not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{store_id}")
    public StoreResponseDTO get(@PathVariable("store_id") Long id) {
        return storeService.get(id);
    }

    @Operation(
            summary = "Get a list of stores",
            description = "Average user is only enabled to get an array of stores from a " +
                    "desired city. Accountants may retrieve all stores from database " +
                    "as well as query only for list of store brands " +
                    "(with <b>brand_only</b> parameter as flag)." +
                    "<br><b>Options available:</b><br>" +
                    "&bull; <b>/api/store</b> - lists all stores in database: <i>for accountant roles only</i><br>" +
                    "&bull; <b>/api/store?brand_only</b> - lists all store brands in database: <i>for accountant roles only</i><br>" +
                    "&bull; <b>/api/store?city=${some_city}</b> - lists every entity in a given city",
            parameters = {
                    @Parameter(
                            name = "city",
                            in = ParameterIn.QUERY,
                            required = false
                    ),
                    @Parameter(
                            name = "brand_only",
                            in = ParameterIn.QUERY,
                            required = false,
                            allowEmptyValue = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Store list retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "&bull; Unauthorized (dummy response)<br>" +
                                    "&bull; City not found",
                            content = @Content
                    )
            }
    )
    @GetMapping(params = "city")
    public List<StoreResponseDTO> getAllInCity(@RequestParam(value = "city", required = false) String city) {
        return storeService.getStores(city);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public List<StoreResponseDTO> getAll() {
        return storeService.getStores();
    }

    @GetMapping(params = "brand_only")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public List<StoreNameDTO> getAllBrands(@RequestParam(value = "brand_only", required = false) Boolean brandOnly) {
        return storeService.getAllBrands();
    }

    @Operation(
            summary = "Add new store",
            description = "Hey, if you just opened up a new store, "
                    + "do not hesitate to tell us so!" +
                    "<br><b>CONSTRAINTS:</b><br>" +
                    "&bull; name, street and city must not be left empty",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"name\":\"Carrefour\",\"city\":\"Paris\",\"street\":\"79 Rue de Seine\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Store successfully created",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = StoreResponseDTO.class
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
                            description = "Unauthorized (dummy response)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Such store already exists",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @PostMapping
    public ResponseEntity<StoreResponseDTO> add(@RequestBody @Valid StoreRequestDTO storeRequestDTO) {
        StoreResponseDTO saved = storeService.add(storeRequestDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(saved);
    }

    @Operation(
            summary = "Replace store",
            description = "Replace a store that, for example, you might have seen " +
                    "closed and replaced by a new one... and keep the <i>id</i>!<br>" +
                    "<b>WARNING:</b> every price associated with the previous store " +
                    "will be deleted! Plus, the image may also be deleted if you replaced " +
                    "the last appearance of store brand." +
                    "<br><b>CONSTRAINTS:</b><br>" +
                    "&bull; same constraints apply as with the case of usual store addition",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"name\":\"Lidl\",\"city\":\"Madrid\",\"street\":\"C. de Bravo Murillo, 121\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "&bull; Store successfully replaced<br>" +
                                    "&bull; Replacement is the same as original entity: nothing changes",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = StoreResponseDTO.class
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
                                    "&bull; Store not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Such store already exists",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @PutMapping("/{store_id}")
    public StoreResponseDTO replace(@PathVariable("store_id") Long storeId,
                                    @RequestBody @Valid StoreRequestDTO requestDTO) {
        return storeService.replace(storeId, requestDTO);
    }

    @Operation(
            summary = "Update store",
            description = "If you made a typo in - for instance - street number, then you are " +
                    "welcome to fix it here without creating an entirely new object.<br>" +
                    "<b>WARNING:</b> No matter what field you replace, " +
                    "all of the store prices will, of course, be deleted! " +
                    "Plus, the image may also be deleted if you replaced " +
                    "the last appearance of store brand." +
                    "<br><b>CONSTRAINTS:</b><br>" +
                    "&bull; brand, city and street, if specified in the request body, must not be empty<br>",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "Updating name",
                                            value = "{\"name\":\"Walmart\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Updating name, city & street",
                                            value = "{\"name\":\"Tesco\", \"city\":\"London\",\"street\":\"15 Great Suffolk St\"}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "&bull; Store successfully updated<br>" +
                                    "&bull; Replacement is the same as original entity: nothing changes",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = StoreResponseDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "&bull; Validation failed<br>" +
                                    "&bull; Request body was empty",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "&bull; Unauthorized (dummy response)<br>" +
                                    "&bull; Store not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Such store already exists",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @PatchMapping("/{store_id}")
    public StoreResponseDTO update(@PathVariable("store_id") Long storeId,
                                   @RequestBody @Valid StoreUpdateDTO updateDTO) {
        return storeService.update(storeId, updateDTO);
    }

    @Operation(
            summary = "Delete store by id",
            description = "You've just gone bankrupt... again, haven't you?" +
                    " Well, what a shame...<br> But please, " +
                    "do remember to delete it from <b>Alkolicznik&trade;</b>... Thanks!",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Store successfully deleted",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = StoreDeleteDTO.class
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
    @DeleteMapping("/{store_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public StoreDeleteDTO deleteByParam(@PathVariable("store_id") Long storeId) {
        return storeService.delete(storeId);
    }

    @Operation(
            summary = "Delete store by object",
            description = "In case you've long forgotten the store's ID, then you still " +
                    "are given the option to delete by store's properties.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Deleting store",
                                    value = "{\"name\":\"Kaufland\",\"city\":\"Berlin\",\"street\":\"Storkower Str. 139\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Store successfully deleted",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = StoreRequestDTO.class
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
                                    "&bull; Store not found",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @DeleteMapping
    public StoreDeleteDTO deleteByObject(@RequestBody @Valid StoreRequestDTO request) {
        return storeService.delete(request);
    }
}

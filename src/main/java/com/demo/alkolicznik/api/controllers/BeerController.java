package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.dto.beer.*;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/beer")
@Tag(name = "Beer")
@RequiredArgsConstructor
public class BeerController {

    private final BeerService beerService;

    @GetMapping("/{beer_id}")
    @Operation(
            summary = "Get beer details",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Beer details retrieved",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BeerResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Beer not found",
                            content = @Content
                    )
            }
    )
    public BeerResponseDTO get(@PathVariable("beer_id") Long id) {
        return beerService.get(id);
    }

    @GetMapping(params = "city")
    @Operation(
            summary = "Get a list of all beers",
            description = "Average user is only enabled to get an array of beers from a " +
                    "desired city. Accountants may retrieve all beers from database." +
                    "<br><b>Options available:</b><br>" +
                    "&bull; <b>/api/beer</b> - lists every beer in database: <i>for accountant roles only</i><br>" +
                    "&bull; <b>/api/beer?city=${some_city}</b> - lists every entity in a given city",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Beer list retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "&bull; Unauthorized (dummy response)<br>" +
                                    "&bull; City not found",
                            content = @Content
                    ),
            }
    )
    public List<BeerResponseDTO> getAllInCity(@RequestParam(value = "city", required = false) String city) {
        return beerService.getBeers(city);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public List<BeerResponseDTO> getAll() {
        return beerService.getBeers();
    }

    @Operation(
            summary = "Add new beer",
            description = "If you found some beer missing, then by all means add it!<br>" +
                    "<b>NOTE:</b> if you do not specify beer's volume, then it will be set to 0.5 by default" +
                    "<br><b>CONSTRAINTS:</b><br>" +
                    "&bull; brand must be specified (type may be empty though)<br>" +
                    "&bull; volume must be a positive number<br>" +
                    "&bull; beer must not have been already created",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"brand\":\"Heineken\",\"type\":\"Silver\",\"volume\":0.5}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Beer successfully created",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = BeerResponseDTO.class
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
                            description = "Such beer already exists",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @PostMapping
    public ResponseEntity<BeerResponseDTO> add(@RequestBody @Valid BeerRequestDTO beerRequestDTO) {
        BeerResponseDTO savedDTO = beerService.add(beerRequestDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedDTO.getId())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(savedDTO);
    }

    @Operation(
            summary = "Replace beer",
            description = "Here you can replace an already existing beer with new one. " +
                    "Features? You can keep the <i>id</i>! How cool is that?<br>" +
                    "<b>NOTE:</b> if you do not specify beer's volume, then it will be set to 0.5 by default" +
                    "<b>WARNING:</b> every price & image associated " +
                    "with the previous beer will be deleted!" +
                    "<br><b>CONSTRAINTS:</b><br>" +
                    "&bull; same constraints apply as with the case of usual beer addition",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"brand\":\"Carlsberg\",\"type\":\"Pilsner\",\"volume\":0.33}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "&bull; Beer successfully replaced<br>" +
                                    "&bull; Replacement is the same as original entity: nothing changes",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = BeerResponseDTO.class
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
                                    "&bull; Beer not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Such beer already exists",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @PutMapping("/{beer_id}")
    public BeerResponseDTO replace(@PathVariable("beer_id") Long beerId,
                                   @RequestBody @Valid BeerRequestDTO requestDTO) {
        return beerService.replace(beerId, requestDTO);
    }

    @Operation(
            summary = "Update beer",
            description = "If you are interested in updating just one or two individual " +
                    "pieces of an item, you've come to the right place.<br>" +
                    "<b>NOTE:</b> if you do not specify beer's volume, then it will be set to 0.5 by default" +
                    "<b>TIP:</b> to remove beer's type, put an empty string as a value of \"type\" key (see example).<br>" +
                    "<b>WARNING:</b> If you update anything else than volume, " +
                    "then all associated prices & an image will be deleted." +
                    "<br><b>CONSTRAINTS:</b><br>" +
                    "&bull; brand, if specified in the request body, must not be empty<br>" +
                    "&bull; volume must be a positive number",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "Updating brand & type",
                                            value = "{\"brand\":\"Corona\",\"type\":\"Extra\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Updating brand & removing type",
                                            value = "{\"brand\":\"Budweiser\",\"type\":\"\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Updating volume",
                                            value = "{\"volume\":0.33}"
                                    ),
                                    @ExampleObject(
                                            name = "Updating brand, type & volume",
                                            value = "{\"brand\":\"Kingfisher\",\"type\":\"Strong\",\"volume\":0.6}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Replacement is the same as original entity: nothing changes",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "201",
                            description = "Beer successfully updated",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = BeerResponseDTO.class
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
                            description = "&bull; Beer not found<br>" +
                                    "&bull; Unauthorized (dummy response)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Such beer already exists",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @PatchMapping("/{beer_id}")
    public BeerResponseDTO update(@PathVariable("beer_id") Long beerId,
                                  @RequestBody @Valid BeerUpdateDTO updateDTO) {
        return beerService.update(beerId, updateDTO);
    }

    @Operation(
            summary = "Delete beer by id",
            description = "Please remove a beer that is nowhere to be seen anymore in order not to confuse customers...",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Beer successfully deleted",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = BeerDeleteResponseDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "&bull; Unauthorized (dummy response)<br>" +
                                    "&bull; Beer not found",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @DeleteMapping("/{beer_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public BeerDeleteResponseDTO delete(@PathVariable("beer_id") Long beerId) {
        return beerService.delete(beerId);
    }

    @Operation(
            summary = "Delete beer by JSON string",
            description = "The beer should have been deleted years ago and now - even worse - you can't get its ID?<br>"
                    + "Not a problem! Try to describe it just as you'd create it de novo.<br>" +
                    "<b>NOTE:</b> if you do not specify beer's volume, then it will be set to 0.5 by default",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "Deleting a beer w/o type & volume",
                                            description = "This will delete a beer <b>Blackfort</b> of <b>0.5</b> volume",
                                            value = "{\"brand\":\"Blackfort\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Deleting a beer with beer, type & volume specified",
                                            value = "{\"brand\":\"Godfather\",\"type\":\"Lager\",\"volume\":0.33}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Beer successfully deleted",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = BeerDeleteResponseDTO.class
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
                                    "&bull; Beer not found",
                            content = @Content
                    ),
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @DeleteMapping
    public BeerDeleteResponseDTO delete(@RequestBody @Valid BeerDeleteRequestDTO requestDTO) {
        return beerService.delete(requestDTO);
    }
}

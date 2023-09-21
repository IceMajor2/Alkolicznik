package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.dto.beer.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
public class BeerController {

    private BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @GetMapping("/{beer_id}")
    @Operation(
            summary = "Get beer object",
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
            description = "Average user is only enabled to get an array of beers from a "
                    + "desired city. Accountant roles may retrieve all beers from database."
                    + "<br><b>Options available</b>:<br>"
                    + "&bull; <b>/api/beer</b> - lists every beer in database: <i>for accountant roles only</i><br>"
                    + "&bull; <b>/api/beer?city=${some_city}</b> - lists every in a given city",
            parameters = @Parameter(
                    name = "city",
                    in = ParameterIn.PATH,
                    required = false
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Beer list retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "&bull; Unauthorized (dummy response)<br>" +
                                    "&bull; City not found ",
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
            description = "If you found some beer missing, then by all means add it!" +
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

    @Operation(summary = "Update beer",
            description = "If you are interested in updating just one or two - or even more - individual "
                    + "fields, you've come to the right place.<br>"
                    + "<b>WARNING #1:</b> If you update brand and/or type, the beer will be removed "
                    + "from each store it has been previously linked to.<br>"
                    + "<b>WARNING #2:</b> If you update anything else than beer volume, then "
                    + "beer image will be deleted.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "beer successfully updated"),
            @ApiResponse(responseCode = "200", description = "replacement is the same as original entity - nothing happens", content = @Content),
            @ApiResponse(responseCode = "400", description = "provided data violates constraints", content = @Content),
            @ApiResponse(responseCode = "400 (2)", description = "there was not one single property to update specified", content = @Content),
            @ApiResponse(responseCode = "404", description = "resource not found - dummy response "
                    + "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
            @ApiResponse(responseCode = "404 (2)", description = "beer not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "such beer already exists", content = @Content)
    })
    @PatchMapping("/{beer_id}")
    @SecurityRequirement(name = "JWT Authentication")
    public BeerResponseDTO update(@PathVariable("beer_id") Long beerId,
                                  @RequestBody @Valid BeerUpdateDTO updateDTO) {
        return beerService.update(beerId, updateDTO);
    }

    @Operation(summary = "Delete beer by id",
            description = "Was this beer a limited edition? Is it nowhere to be acquired anymore?<br>"
                    + "Say no more! Just give me an ID...")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "beer successfully deleted"),
            @ApiResponse(responseCode = "404", description = "resource not found - dummy response "
                    + "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
            @ApiResponse(responseCode = "404 (2)", description = "beer not found", content = @Content)
    })
    @DeleteMapping("/{beer_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @SecurityRequirement(name = "JWT Authentication")
    public BeerDeleteResponseDTO delete(@PathVariable("beer_id") Long beerId) {
        return beerService.delete(beerId);
    }

    @Operation(summary = "Delete beer by JSON string",
            description = "The beer is nowhere to be found anymore and - even worse - you can't get its ID?<br>"
                    + "Not a problem! Try to describe it just as you'd create it de novo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "beer successfully deleted", content = @Content),
            @ApiResponse(responseCode = "400", description = "provided data violates constraints", content = @Content),
            @ApiResponse(responseCode = "404", description = "resource not found - dummy response "
                    + "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
            @ApiResponse(responseCode = "404 (2)", description = "beer not found", content = @Content)
    })
    @DeleteMapping
    @SecurityRequirement(name = "JWT Authentication")
    public BeerDeleteResponseDTO delete(@RequestBody @Valid BeerDeleteRequestDTO requestDTO) {
        return beerService.delete(requestDTO);
    }
}

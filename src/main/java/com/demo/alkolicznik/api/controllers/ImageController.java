package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerImageService;
import com.demo.alkolicznik.api.services.StoreImageService;
import com.demo.alkolicznik.dto.image.BeerImageResponseDTO;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.dto.image.StoreImageResponseDTO;
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
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(
        name = "Image",
        description = "Uses ImageKit external API"
)
public class ImageController {

    private final StoreImageService storeImageService;

    private final BeerImageService beerImageService;


    @Operation(
            summary = "Get beer image",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Image retrieved",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = BeerImageResponseDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "&bull; Beer not found<br>" +
                                    "&bull; Image not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/beer/{beer_id}/image")
    public BeerImageResponseDTO getBeerImage(@PathVariable("beer_id") Long beerId) {
        return beerImageService.get(beerId);
    }

    @Operation(
            summary = "Get a list of beer images",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Image list retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Unauthorized (dummy response)",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @GetMapping("/beer/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public List<BeerImageResponseDTO> getAllBeerImages() {
        return beerImageService.getAll();
    }

    @Operation(
            summary = "Add new beer image",
            description = "On successful addition, the image will be sent to ImageKit account as well." +
                    "<br><br><b>CONSTRAINTS:</b><br>" +
                    "&bull; image's proportions must be close to 3:7<br>" +
                    "&bull; path must be absolute and separated with double backslashes (see example)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"image_path\":\"C:\\\\Users\\\\alkolicznik\\\\beer.png\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Image successfully created",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = BeerImageResponseDTO.class
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
                            description = "Image already exists",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @PostMapping("/beer/{beer_id}/image")
    public ResponseEntity<BeerImageResponseDTO> addBeerImage(
            @PathVariable("beer_id") Long beerId,
            @RequestBody @Valid ImageRequestDTO request) {
        BeerImageResponseDTO response = beerImageService.add(beerId, request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(beerId)
                .toUri();
        return ResponseEntity
                .created(location)
                .body(response);
    }

    @Operation(
            summary = "Delete beer image",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Image successfully deleted",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = Map.class
                                    ),
                                    examples = @ExampleObject(
                                            value = "{\"status\":\"message\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "&bull; Unauthorized (dummy response)<br>" +
                                    "&bull; Beer not found<br>" +
                                    "&bull; Image not found",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @DeleteMapping("/beer/{beer_id}/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity deleteBeerImage(@PathVariable("beer_id") Long beerId) {
        beerImageService.delete(beerId);
        return ResponseEntity.ok(Map.of("message", "Beer image was deleted successfully!"));
    }

    @Operation(
            summary = "Get store image by store id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Store image retrieved",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = StoreImageResponseDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "&bull; Store not found<br>" +
                                    "&bull; Image not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/store/{store_id}/image")
    public StoreImageResponseDTO getStoreImage(@PathVariable("store_id") Long storeId) {
        return storeImageService.get(storeId);
    }

    @Operation(
            summary = "Get store image by store name",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Store image retrieved",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = StoreImageResponseDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "&bull; Store not found<br>" +
                                    "&bull; Image not found",
                            content = @Content
                    )
            }
    )
    @GetMapping(value = "/store/image", params = "name")
    public StoreImageResponseDTO getStoreImage(@RequestParam("name") String storeName) {
        storeName = storeName.replace("%20", " ");
        return storeImageService.get(storeName);
    }

    @Operation(
            summary = "Get a list of store images",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Store image list retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Unauthorized (dummy response)",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @GetMapping("/store/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public List<StoreImageResponseDTO> getAllStoreImages() {
        return storeImageService.getAll();
    }

    @Operation(
            summary = "Add new store image",
            description = "On successful addition, the image will be sent to ImageKit account as well." +
                    "<br><br><b>CONSTRAINTS:</b><br>" +
                    "&bull; image's proportions must not exceed 7:2<br>" +
                    "&bull; path must be absolute and separated with double backslashes (see example)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"image_path\":\"C:\\\\Users\\\\alkolicznik\\\\store.png\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Store image successfully created",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = StoreImageResponseDTO.class
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
                            description = "Image already exists",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @PostMapping("/store/image")
    public ResponseEntity<StoreImageResponseDTO> addStoreImage(
            @RequestParam("name") String storeName,
            @RequestBody @Valid ImageRequestDTO imageRequestDTO) {
        // space in a path is represented by '%20', thus we need to
        // replace it with actual space char to get a valid name
        storeName = storeName.replace("%20", " ");
        StoreImageResponseDTO response = storeImageService.add(storeName, imageRequestDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .queryParam("name", storeName)
                .build()
                .toUri();
        return ResponseEntity
                .created(location)
                .body(response);
    }

    @Operation(
            summary = "Replace store image",
            description = "Same constraints - as in the case with adding a store image - apply here.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"image_path\":\"C:\\\\Users\\\\alkolicznik\\\\store.png\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Store image successfully replaced",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = StoreImageResponseDTO.class
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
                                    "&bull; Store not found<br>" +
                                    "&bull; Image not found",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @PutMapping("/store/image")
    public StoreImageResponseDTO replaceStoreImage(@RequestParam("name") String storeName,
                                                   @RequestBody @Valid ImageRequestDTO imageRequestDTO) {
        storeName = storeName.replace("%20", " ");
        return storeImageService.replace(storeName, imageRequestDTO);
    }

    @Operation(
            summary = "Delete store image",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Image successfully deleted",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = Map.class
                                    ),
                                    examples = @ExampleObject(
                                            value = "{\"status\":\"message\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "&bull; Unauthorized (dummy response)<br>" +
                                    "&bull; Store not found<br>" +
                                    "&bull; Image not found",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @DeleteMapping("/store/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity deleteStoreImage(@RequestParam("name") String storeName) {
        storeName = storeName.replace("%20", " ");
        storeImageService.delete(storeName);
        return ResponseEntity.ok(Map.of("message", "Store image was deleted successfully!"));
    }
}

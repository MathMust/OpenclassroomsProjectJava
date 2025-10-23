package com.rental.controllers;

import com.rental.constants.Constants;
import com.rental.dto.*;
import com.rental.services.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class RentalController {

    private final RentalService rentalService;

    @Value("${rental.upload-dir}")
    private String uploadDir;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @Operation(summary = "Création d’un logement",
            description = "Permet de créer un logement. Les champs textuels sont envoyés via FormData, et la photo du logement est un fichier multipart.<br/>" +
                    "Pour tester laisser le champs « picturePath » vide.")
    @ApiResponse(responseCode = Constants.API_STATUS_OK, description = "Logement créé avec succès",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RentalResponse.class)))
    @ApiResponse(responseCode = Constants.API_STATUS_UNAUTHORIZED, description = Constants.USER_UNAUTHORIZED,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(value = "/rentals", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRental(@ModelAttribute RentalRequest rentalRequest,
                                          Authentication authentication,
                                          HttpServletRequest request) throws IOException {
        String email = authentication.getName();

        MultipartFile picture = rentalRequest.getPicture();
        String pictureUrl = null;

        if (picture != null && !picture.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + picture.getOriginalFilename();
            Path uploadPath = Paths.get(System.getProperty("user.dir")).resolve(uploadDir);
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            picture.transferTo(filePath.toFile());

            // Construire l'URL complète pour le front
            String baseUrl = String.format("%s://%s:%d",
                    request.getScheme(),
                    request.getServerName(),
                    request.getServerPort());
            pictureUrl = baseUrl + "/uploads/" + fileName;
        }

        rentalRequest.setPicturePath(pictureUrl);
        rentalService.createRental(rentalRequest, email);

        return ResponseEntity.ok(new RentalResponse("Rental created !"));
    }

    @Operation(summary = "Liste des logements",
            description = "Retourne la liste complète des logements disponibles.")
    @ApiResponse(responseCode = Constants.API_STATUS_OK, description = "Liste des logements récupérée avec succès",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RentalsResponse.class)))
    @ApiResponse(responseCode = Constants.API_STATUS_UNAUTHORIZED, description = Constants.USER_UNAUTHORIZED,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/rentals")
    public ResponseEntity<?> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    @Operation(summary = "Détails d’un logement",
            description = "Renvoie les informations détaillées d’un logement à partir de son ID.")
    @ApiResponse(responseCode = Constants.API_STATUS_OK, description = "Détails du logement récupérés avec succès",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RentalDto.class)))
    @ApiResponse(responseCode = Constants.API_STATUS_UNAUTHORIZED, description = Constants.USER_UNAUTHORIZED,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = Constants.API_STATUS_NOT_FOUND, description = Constants.RENTAL_NOT_FOUND_FR,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/rentals/{id}")
    public ResponseEntity<RentalDto> getRentalById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.getRentalById(id));
    }

    @Operation(summary = "Mise à jour d’un logement",
            description = "Permet de modifier les informations d’un logement existant, sans modifier l’image.<br>" +
                    "Dans le paramètre « rentalRequest » supprimer les données « picture » et « picturePath » pour tester")
    @ApiResponse(responseCode = Constants.API_STATUS_OK, description = "Logement mis à jour avec succès",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RentalResponse.class)))
    @ApiResponse(responseCode = Constants.API_STATUS_UNAUTHORIZED, description = Constants.USER_UNAUTHORIZED,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = Constants.API_STATUS_NOT_FOUND, description = Constants.RENTAL_NOT_FOUND_FR,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping(value = "/rentals/{id}")
    public ResponseEntity<?> updateRental(@PathVariable Long id,
                                          @ModelAttribute RentalRequest rentalRequest,
                                          Authentication authentication) {
        String email = authentication.getName();
        rentalRequest.setPicture(null);
        rentalRequest.setPicturePath(null);
        rentalService.updateRental(id, rentalRequest, email);
        return ResponseEntity.ok(new RentalResponse("Rental updated !"));
    }

}

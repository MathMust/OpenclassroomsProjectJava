package com.rental.controllers;

import com.rental.constants.Constants;
import com.rental.dto.ErrorResponse;
import com.rental.dto.UserResponse;
import com.rental.model.DBUser;
import com.rental.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Operation(summary = "Récupère un utilisateur par son ID",
            description = "Renvoie les informations d'un utilisateur. Nécessite d'être authentifié.")
    @ApiResponse(responseCode = Constants.API_STATUS_OK, description = "Utilisateur trouvé avec succès",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class)))
    @ApiResponse(responseCode = Constants.API_STATUS_UNAUTHORIZED, description = Constants.USER_UNAUTHORIZED,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = Constants.API_STATUS_NOT_FOUND, description = "Utilisateur non trouvé",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable int id) {
        DBUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(Constants.USER_NOT_FOUND));

        return ResponseEntity.ok(new UserResponse(user));
    }
}

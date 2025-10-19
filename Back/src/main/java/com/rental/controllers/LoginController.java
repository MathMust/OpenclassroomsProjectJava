package com.rental.controllers;

import com.rental.constants.Constants;
import com.rental.dto.*;
import com.rental.model.DBUser;
import com.rental.repository.UserRepository;
import com.rental.services.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    public LoginService loginService;
    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository, LoginService loginService) {
        this.userRepository = userRepository;
        this.loginService = loginService;
    }

    @Operation(summary = "Enregistre un nouvel utilisateur")
    @ApiResponse(responseCode = Constants.API_STATUS_OK, description = "Utilisateur enregistré avec succès",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthSuccess.class)))
    @ApiResponse(responseCode = Constants.API_STATUS_BAD_REQUEST, description = "Requête invalide ou utilisateur existant",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
            String token = loginService.register(request);
            return ResponseEntity.ok().body(new AuthSuccess(token));
    }

    @Operation(summary = "Récupère les informations de l’utilisateur connecté")
    @ApiResponse(responseCode = Constants.API_STATUS_OK, description = "Infos de l’utilisateur connecté",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class)))
    @ApiResponse(responseCode = Constants.API_STATUS_UNAUTHORIZED, description = "Non authentifié ou token invalide",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        DBUser user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException(Constants.USER_NOT_FOUND));

        return ResponseEntity.ok(new UserResponse(user));
    }

    @Operation(summary = "Connexion utilisateur")
    @ApiResponse(responseCode = Constants.API_STATUS_OK, description = "Connexion réussie",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthSuccess.class)))
    @ApiResponse(responseCode = Constants.API_STATUS_UNAUTHORIZED, description = "Email ou mot de passe incorrect",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = loginService.login(request);
        return ResponseEntity.ok(new AuthSuccess(token));
    }
}

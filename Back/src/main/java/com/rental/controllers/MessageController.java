package com.rental.controllers;

import com.rental.constants.Constants;
import com.rental.dto.ErrorResponse;
import com.rental.dto.MessageRequest;
import com.rental.dto.MessageResponse;
import com.rental.services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(summary = "Envoi d’un message",
            description = "Permet à un utilisateur authentifié d’envoyer un message concernant un logement.")
    @ApiResponse(responseCode = Constants.API_STATUS_OK, description = "Message envoyé avec succès",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class)))
    @ApiResponse(responseCode = Constants.API_STATUS_BAD_REQUEST, description = "Requête invalide (ex: logement inexistant, utilisateur incorrect)",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = Constants.API_STATUS_UNAUTHORIZED, description = Constants.USER_UNAUTHORIZED,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/messages")
    public ResponseEntity<?> sendMessage(
            @RequestBody MessageRequest messageRequest,
            Authentication authentication
    ) {
        messageService.sendMessage(messageRequest, authentication.getName());
        return ResponseEntity.ok(new MessageResponse("Message send with success"));
    }
}

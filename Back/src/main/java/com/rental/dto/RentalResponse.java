package com.rental.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentalResponse {

    private String message;

    public RentalResponse(String message) {
        this.message = message;
    }
}

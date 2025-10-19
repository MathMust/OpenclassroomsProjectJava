package com.rental.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {

    private Long rental_id;
    private Long user_id;
    private String message;
}

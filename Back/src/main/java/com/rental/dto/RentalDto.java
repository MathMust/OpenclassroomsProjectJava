package com.rental.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class RentalDto {

    private Long id;
    private String name;
    private Double surface;
    private Double price;
    private String picture;
    private String description;
    private int owner_id;
    private Timestamp created_at;
    private Timestamp updated_at;

}
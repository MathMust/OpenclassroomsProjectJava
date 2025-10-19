package com.rental.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RentalsResponse {

    private List<RentalDto> rentals;

}
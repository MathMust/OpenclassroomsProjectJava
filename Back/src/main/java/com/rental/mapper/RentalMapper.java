package com.rental.mapper;

import com.rental.dto.RentalDto;
import com.rental.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RentalMapper {

    @Mapping(source = "owner.id", target = "owner_id")
    RentalDto rentalToRentalDto(Rental rental);

    List<RentalDto> rentalListToRentalDtoList(List<Rental> rentals);
}

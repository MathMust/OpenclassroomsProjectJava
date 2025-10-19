package com.rental.services;

import com.rental.constants.Constants;
import com.rental.dto.RentalDto;
import com.rental.dto.RentalRequest;
import com.rental.dto.RentalsResponse;
import com.rental.mapper.RentalMapper;
import com.rental.model.DBUser;
import com.rental.model.Rental;
import com.rental.repository.RentalRepository;
import com.rental.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final RentalMapper rentalMapper;

    public RentalService(RentalRepository rentalRepository, UserRepository userRepository, RentalMapper rentalMapper) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.rentalMapper = rentalMapper;
    }

    public void createRental(RentalRequest request, String email) {
        DBUser owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(Constants.USER_NOT_FOUND));

        Rental rental = new Rental();
        rental.setName(request.getName());
        rental.setDescription(request.getDescription());
        rental.setPrice(request.getPrice());
        rental.setSurface(request.getSurface());
        rental.setPicture(request.getPicturePath());
        rental.setOwner(owner);
        Timestamp now = Timestamp.from(Instant.now());
        rental.setCreated_at(now);
        rental.setUpdated_at(now);

        rentalRepository.save(rental);
    }

    public RentalsResponse getAllRentals() {
        List<Rental> rentals = rentalRepository.findAll();
        List<RentalDto> rentalsDto = rentalMapper.rentalListToRentalDtoList(rentals);
        RentalsResponse rentalsResponse = new RentalsResponse();
        rentalsResponse.setRentals(rentalsDto);

        return rentalsResponse;
    }

    public RentalDto getRentalById(Long id) {
        Optional<Rental> rental = rentalRepository.findById(id);
        if (rental.isPresent()) {
            return rentalMapper.rentalToRentalDto(rental.get());
        } else {
            throw new IllegalArgumentException(Constants.RENTAL_NOT_FOUND);
        }
    }

    public void updateRental(Long id, RentalRequest request, String userEmail) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(Constants.RENTAL_NOT_FOUND));

        // Vérifie que l’utilisateur connecté est bien le propriétaire
        if (!rental.getOwner().getEmail().equals(userEmail)) {
            throw new SecurityException("You are not allowed to update this rental");
        }

        rental.setName(request.getName());
        rental.setPrice(request.getPrice());
        rental.setSurface(request.getSurface());
        rental.setDescription(request.getDescription());
        rental.setUpdated_at(new Timestamp(System.currentTimeMillis()));
        rentalRepository.save(rental);
    }
}

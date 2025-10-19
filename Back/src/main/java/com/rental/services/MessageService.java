package com.rental.services;

import com.rental.constants.Constants;
import com.rental.dto.MessageRequest;
import com.rental.model.DBUser;
import com.rental.model.Message;
import com.rental.model.Rental;
import com.rental.repository.MessageRepository;
import com.rental.repository.RentalRepository;
import com.rental.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          RentalRepository rentalRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    public void sendMessage(MessageRequest request, String senderEmail) {

        DBUser sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException(Constants.USER_NOT_FOUND));

        Rental rental = rentalRepository.findById(request.getRental_id())
                .orElseThrow(() -> new IllegalArgumentException(Constants.RENTAL_NOT_FOUND));

        if (!Objects.equals(sender.getId(), request.getUser_id())) {
            throw new IllegalArgumentException("User ID does not match the logged in user");
        }

        Message message = new Message();
        message.setUser(sender);
        message.setRental(rental);
        message.setMessage(request.getMessage());
        Timestamp now = Timestamp.from(Instant.now());
        message.setCreated_at(now);
        message.setUpdated_at(now);

        messageRepository.save(message);
    }
}

package com.rental.dto;

import com.rental.model.DBUser;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String email;
    private String name;
    private Timestamp created_at;
    private Timestamp updated_at;

    public UserResponse(DBUser user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.created_at = user.getCreated_at();
        this.updated_at = user.getUpdated_at();
    }
}

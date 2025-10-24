package com.rental.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class RentalRequest {

    private String name;

    private String description;

    private Double price;

    private Double surface;

    private MultipartFile picture;

    private String picturePath;
}

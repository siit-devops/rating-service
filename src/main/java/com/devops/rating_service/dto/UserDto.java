package com.devops.rating_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private UUID id;
    private String username;
    private String firstname;
    private String lastname;
    private double rating;
}

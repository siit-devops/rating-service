package com.devops.rating_service.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RatingDto {
    private UUID id;
    private UserDto guest;
    private double ratingValue;
    private LocalDateTime createdAt;
    private String description;
}

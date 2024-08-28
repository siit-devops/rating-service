package com.devops.rating_service.dto;

import com.devops.rating_service.model.Rating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingsDTO {
    private List<Rating> ratingsList;
    private Double averageRating;
}

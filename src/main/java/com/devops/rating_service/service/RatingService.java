package com.devops.rating_service.service;

import com.devops.rating_service.dto.RatingsDTO;
import com.devops.rating_service.exceptions.NotFoundException;
import com.devops.rating_service.exceptions.RatingRejected;
import com.devops.rating_service.model.Rating;
import com.devops.rating_service.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    public Rating getById(UUID id) {

        Rating rating = ratingRepository.findById(id).orElseThrow(() -> new NotFoundException("Rating is not found!"));
        if(!rating.getActive()) throw new NotFoundException("Rating is deleted!");

        return rating;
    }

    public Rating addRating(Rating newRating, UUID userId) {

        if(newRating.getHostRating() && !userHasPreviousReservations(userId, newRating.getSubjectId()))
            throw new RatingRejected("You didn't have any reservation at this host!");

        if(!newRating.getHostRating() && !userStayedIn(userId, newRating.getSubjectId()))
            throw new RatingRejected("You have not stayed in this accommodation ever!");

        newRating.setGuestId(userId);
        newRating.setCreatedAt(LocalDateTime.now());
        newRating.setActive(true);

        return ratingRepository.save(newRating);
    }

    private boolean userHasPreviousReservations(UUID userId, UUID subjectId) {
        //TODO check previous reservations
        return true;
    }

    private boolean userStayedIn(UUID userId, UUID subjectId) {
        //TODO check if user stayed in accommodation
        return true;
    }

    public Rating updateRating(Rating updatedRating, UUID userId, UUID ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new NotFoundException("Rating is not found!"));

        if(!rating.getActive() || !rating.getGuestId().equals(userId))
            throw new NotFoundException("Rating is not found!");

        updatedRating.setCreatedAt(LocalDateTime.now());
        updatedRating.setActive(true);

        return ratingRepository.save(updatedRating);
    }

    public void deleteRating(UUID userId, UUID id) {
        Rating rating = ratingRepository.findById(id).orElseThrow(() -> new NotFoundException("Rating is not found!"));

        if(!rating.getGuestId().equals(userId))
            throw new RatingRejected("This rating is not your!");

        rating.setActive(false);
        ratingRepository.save(rating);
    }

    public RatingsDTO getAllHostRatings(UUID hostId) {
        List<Rating> ratings = ratingRepository.findAllHostRatings(hostId);
        Double averageRating = ratings.stream().mapToInt(Rating::getRatingValue).average().orElse(0.0);

        return new RatingsDTO(ratings, averageRating);
    }

    public RatingsDTO getAllAccommodationRatings(UUID accommodationId) {
        List<Rating> ratings = ratingRepository.findAllAccommodationRatings(accommodationId);
        Double averageRating = ratings.stream().mapToInt(Rating::getRatingValue).average().orElse(0.0);

        return new RatingsDTO(ratings, averageRating);
    }
}

package com.devops.rating_service.service;

import com.devops.rating_service.dto.RatingDto;
import com.devops.rating_service.dto.RatingsDTO;
import com.devops.rating_service.dto.UserDto;
import com.devops.rating_service.exceptions.NotFoundException;
import com.devops.rating_service.exceptions.RatingRejected;
import com.devops.rating_service.kafka.AccomodationRatingMessage;
import com.devops.rating_service.kafka.HostRatingMessage;
import com.devops.rating_service.kafka.KafkaProducer;
import com.devops.rating_service.model.Rating;
import com.devops.rating_service.repository.RatingRepository;
import com.devops.rating_service.service.feignClients.AccomodationClient;
import com.devops.rating_service.service.feignClients.ReservationClient;
import com.devops.rating_service.service.feignClients.UserClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final KafkaProducer kafkaProducer;

    private final ReservationClient reservationClient;
    private final AccomodationClient accomodationClient;
    private final UserClient userClient;

    public RatingService(RatingRepository ratingRepository, KafkaProducer kafkaProducer, ReservationClient reservationClient, AccomodationClient accomodationClient, UserClient userClient) {
        this.ratingRepository = ratingRepository;
        this.kafkaProducer = kafkaProducer;
        this.reservationClient = reservationClient;
        this.accomodationClient = accomodationClient;
        this.userClient = userClient;
    }

    public Rating getById(UUID id) {

        Rating rating = ratingRepository.findById(id).orElseThrow(() -> new NotFoundException("Rating is not found!"));
        if (!rating.getActive()) throw new NotFoundException("Rating is deleted!");

        return rating;
    }

    public Rating addRating(Rating newRating) {

        if (newRating.getHostRating() && !userHasPreviousReservations(newRating.getGuestId(), newRating.getSubjectId()))
            throw new RatingRejected("You didn't have any reservation at this host!");

        if (!newRating.getHostRating() && !userStayedIn(newRating.getGuestId(), newRating.getSubjectId()))
            throw new RatingRejected("You have not stayed in this accommodation ever!");

        newRating.setCreatedAt(LocalDateTime.now());
        newRating.setActive(true);

        Rating rating = ratingRepository.save(newRating);

        if (rating.getHostRating()) {
            kafkaProducer.send("host-rating", new HostRatingMessage(newRating.getGuestId(),
                    rating.getSubjectId(), rating.getId(), (byte) rating.getRatingValue(), (byte) 0));
        } else {
            UUID hostId = accomodationClient.getHostId(newRating.getSubjectId().toString());
            if (hostId != null) {
                kafkaProducer.send("accommodation-rating", new AccomodationRatingMessage(newRating.getGuestId(), hostId,
                        rating.getSubjectId(), rating.getId(), (byte) rating.getRatingValue()));
            }
        }

        return rating;
    }

    public Rating updateRating(Rating updatedRating, UUID userId, UUID ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new NotFoundException("Rating is not found!"));

        var oldRatingValue = rating.getRatingValue();

        if (!rating.getActive() || !rating.getGuestId().equals(userId))
            throw new NotFoundException("Rating is not found!");

        updatedRating.setCreatedAt(LocalDateTime.now());
        updatedRating.setActive(true);

        ratingRepository.save(updatedRating);

        if (updatedRating.getHostRating()) {
            kafkaProducer.send("host-rating", new HostRatingMessage(userId,
                    rating.getSubjectId(), rating.getId(), (byte) updatedRating.getRatingValue(), (byte) oldRatingValue));
        } else {
            UUID hostId = accomodationClient.getHostId(updatedRating.getSubjectId().toString());
            if (hostId != null) {
                kafkaProducer.send("accommodation-rating", new AccomodationRatingMessage(userId, hostId,
                        rating.getSubjectId(), rating.getId(), (byte) rating.getRatingValue()));
            }
        }

        return updatedRating;
    }

    public void deleteRating(UUID userId, UUID id) {
        Rating rating = ratingRepository.findById(id).orElseThrow(() -> new NotFoundException("Rating is not found!"));

        if (!rating.getGuestId().equals(userId))
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

    public Rating getRatingForHost(UUID subjectId, UUID guestId) {
        return ratingRepository.findRatingByHostRatingTrueAndGuestIdAndSubjectId(guestId, subjectId).orElse(null);
    }

    public Rating getRatingForAccommodation(UUID subjectId, UUID guestId) {
        return ratingRepository.findRatingByHostRatingFalseAndGuestIdAndSubjectId(guestId, subjectId).orElse(null);
    }

    private boolean userHasPreviousReservations(UUID userId, UUID hostId) {
        return reservationClient.checkIfUserHasReservation(userId.toString(), hostId.toString());
    }

    private boolean userStayedIn(UUID userId, UUID accomodationIn) {
        return reservationClient.userStayedAtAccommodation(userId.toString(), accomodationIn.toString());
    }

    public List<RatingDto> getAllRatingsForAcc(UUID id) {
        var ratings = ratingRepository.findAllAccommodationRatings(id);
        List<RatingDto> ratingDtos = new ArrayList<>();
        for (var rating : ratings) {
            var user = userClient.findById(rating.getGuestId());
            ratingDtos.add(RatingDto.builder()
                    .id(rating.getId())
                    .ratingValue(rating.getRatingValue())
                    .createdAt(rating.getCreatedAt())
                    .description(rating.getDescription())
                    .guest(user)
                    .build()
            );
        }
        return ratingDtos;
    }

    public List<RatingDto> getAllRatingsForHost(UUID id) {
        var ratings = ratingRepository.findAllHostRatings(id);
        List<RatingDto> ratingDtos = new ArrayList<>();
        for (var rating : ratings) {
            var user = userClient.findById(rating.getGuestId());
            ratingDtos.add(RatingDto.builder()
                .id(rating.getId())
                .ratingValue(rating.getRatingValue())
                .createdAt(rating.getCreatedAt())
                .description(rating.getDescription())
                .guest(user)
                .build()
            );
        }
        return ratingDtos;
    }
}

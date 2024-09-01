package com.devops.rating_service.controller;

import com.devops.rating_service.dto.RatingsDTO;
import com.devops.rating_service.model.Rating;
import com.devops.rating_service.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/rating")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/{id}")
    public Rating getRatingById(@PathVariable UUID id){
        return ratingService.getById(id);
    }

    @GetMapping("/host")
    //@HasRole("GUEST")
    public Rating getRatingByHostId(@RequestParam UUID hostId, UUID userId){
        return ratingService.getRatingForHost(hostId, userId);
    }

    @GetMapping("/accomodation")
    //@HasRole("GUEST")
    public Rating getRatingByAccommodationId(@RequestParam UUID accommodationId, UUID userId){
        return ratingService.getRatingForAccommodation(accommodationId, userId);
    }

    @PostMapping
    //@HasRole("GUEST")
    public Rating addRating(@RequestBody Rating newRating, UUID userId) {
        return ratingService.addRating(newRating, userId);
    }

    @PutMapping("/{id}")
    //@HasRole("GUEST")
    public Rating updateRating(@PathVariable UUID id, @RequestBody Rating updatedRating, UUID userId) {
        return ratingService.updateRating(updatedRating, userId, id);
    }

    @DeleteMapping("/{id}")
    //@HasRole("GUEST")
    public void deleteRating(@PathVariable UUID id, UUID userId) {
        ratingService.deleteRating(userId, id);
    }

    @GetMapping("/host/{id}")
    public RatingsDTO getAllRatingsForHost(@PathVariable UUID id) {
        return ratingService.getAllHostRatings(id);
    }

    @GetMapping("/accommodation/{id}")
    public RatingsDTO getAllRatingsForAccommodation(@PathVariable UUID id) {
        return ratingService.getAllAccommodationRatings(id);
    }
}

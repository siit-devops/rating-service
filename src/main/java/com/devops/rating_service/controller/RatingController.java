package com.devops.rating_service.controller;

import com.devops.rating_service.dto.RatingDto;
import com.devops.rating_service.dto.RatingsDTO;
import com.devops.rating_service.model.Rating;
import com.devops.rating_service.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
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
    public Rating getRatingByHostId(@RequestParam UUID hostId, Principal principal){
        return ratingService.getRatingForHost(hostId, UUID.fromString(principal.getName()));
    }

    @GetMapping("/accomodation")
    //@HasRole("GUEST")
    public Rating getRatingByAccommodationId(@RequestParam UUID accommodationId, Principal principal){
        return ratingService.getRatingForAccommodation(accommodationId, UUID.fromString(principal.getName()));
    }

    @PostMapping
    //@HasRole("GUEST")
    public Rating addRating(@RequestBody Rating newRating) {
        return ratingService.addRating(newRating);
    }

    @PutMapping("/{id}")
    //@HasRole("GUEST")
    public Rating updateRating(@PathVariable UUID id, @RequestBody Rating updatedRating, Principal principal) {
        return ratingService.updateRating(updatedRating, UUID.fromString(principal.getName()), id);
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

    @GetMapping("/accommodation/{id}/all")
    public List<RatingDto> getAllAccommodationRatings(@PathVariable UUID id) {
        return ratingService.getAllRatingsForAcc(id);
    }

    @GetMapping("/host/{id}/all")
    public List<RatingDto> getAllHostRatings(@PathVariable UUID id) {
        return ratingService.getAllRatingsForHost(id);
    }
}

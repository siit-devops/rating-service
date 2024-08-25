package com.devops.rating_service.repository;

import com.devops.rating_service.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    @Query("select r from Rating r where r.active = true and r.hostRating = true and r.subjectId = ?1")
    List<Rating> findAllHostRatings(UUID subjectId);

    @Query("select r from Rating r where r.active = true and r.hostRating = false and r.subjectId = ?1")
    List<Rating> findAllAccommodationRatings(UUID accommodationId);
}

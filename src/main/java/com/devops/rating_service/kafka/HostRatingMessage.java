package com.devops.rating_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostRatingMessage implements Serializable {
    private UUID guestId;
    private UUID hostId;
    private UUID ratingId;
    private Byte ratingValue;
    private Byte oldRatingValue;
}

package com.devops.rating_service.service.feignClients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "reservation-service", url = "${reservation-service.ribbon.listOfServers}")
public interface ReservationClient {

    @GetMapping("/api/internal/reservations/has-previous/{userId}")
    boolean checkIfUserHasReservation(@PathVariable String userId, @RequestParam String hostId);

    @GetMapping("/api/internal/reservations/stayed/{userId}")
    boolean userStayedAtAccommodation(@PathVariable String userId, @RequestParam String accomodationId);
}

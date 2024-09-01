package com.devops.rating_service.service.feignClients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "accommodation-service", url = "${accommodation-service.ribbon.listOfServers}")
public interface AccomodationClient {

    @GetMapping("/api/internal/accommodations/host/{accommodationId}")
    UUID getHostId(@PathVariable String accommodationId);
}

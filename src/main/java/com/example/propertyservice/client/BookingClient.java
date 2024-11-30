package com.example.propertyservice.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingClient {

    private final WebClient webClient;

    public BookingClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8082/api/v1/bookings").build();
    }

    public Boolean isPropertyAvailable(Integer propertyId, LocalDate checkIn, LocalDate checkOut) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/availability")
                        .queryParam("propertyId", propertyId)
                        .queryParam("checkIn", checkIn)
                        .queryParam("checkOut", checkOut)
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    public List<LocalDate> getAvailableDates(Integer propertyId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/available-dates")
                        .queryParam("propertyId", propertyId)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<LocalDate>>() {})
                .block();
    }
}

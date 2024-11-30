package com.example.propertyservice.dto;

import com.example.propertyservice.models.PropertyFeature;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class PropertyDTO {
    @NotNull(message = "Owner id should not be empty!")
    private Integer ownerId;

    @NotEmpty(message = "Title should not be empty!")
    private String title;

    @NotEmpty(message = "Description should not be empty!")
    private String description;

    @NotEmpty(message = "Location should not be empty!")
    private String location;

    @NotNull(message = "Price per night should not be empty!")
    private BigDecimal pricePerNight;

    @NotNull(message = "Capacity should not be empty!")
    private Integer capacity;

    private Set<PropertyFeatureDTO> features;
}

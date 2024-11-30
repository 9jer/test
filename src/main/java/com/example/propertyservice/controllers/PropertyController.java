package com.example.propertyservice.controllers;

import com.example.propertyservice.dto.AvailableDatesResponse;
import com.example.propertyservice.dto.PropertiesResponse;
import com.example.propertyservice.dto.PropertyDTO;
import com.example.propertyservice.models.Property;
import com.example.propertyservice.services.PropertyService;
import com.example.propertyservice.util.ErrorsUtil;
import com.example.propertyservice.util.PropertyErrorResponse;
import com.example.propertyservice.util.PropertyException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {

    private final PropertyService propertyService;
    private final ModelMapper modelMapper;

    public PropertyController(PropertyService propertyService, ModelMapper modelMapper) {
        this.propertyService = propertyService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createProperty(@RequestBody @Valid PropertyDTO propertyDTO,
                                                     BindingResult bindingResult) {
        Property property = convertPropertyDTOToProperty(propertyDTO);

        if(bindingResult.hasErrors()) {
            ErrorsUtil.returnAllErrors(bindingResult);
        }

        propertyService.save(property);

        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> updateProperty(@PathVariable("id") Integer id, @RequestBody @Valid PropertyDTO propertyDTO, BindingResult bindingResult) {
        Property property = convertPropertyDTOToProperty(propertyDTO);

        if(bindingResult.hasErrors()) {
            ErrorsUtil.returnAllErrors(bindingResult);
        }

        propertyService.updatePropertyById(id, property);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProperty(@PathVariable("id") Integer id) {
        propertyService.delete(id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping
    public PropertiesResponse getAllProperties() {
        return new PropertiesResponse(propertyService.findAll().stream()
                .map(this::convertPropertyToPropertyDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public PropertyDTO getPropertyById(@PathVariable("id") Integer id) {
        return convertPropertyToPropertyDTO(propertyService.getPropertyById(id));
    }

    @GetMapping("/search")
    public List<Property> searchProperties(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return propertyService.search(location, minPrice, maxPrice);
    }


    @GetMapping("/{id}/availability")
    public Boolean checkAvailability(@PathVariable Integer id,
                                     @RequestParam LocalDate checkIn,
                                     @RequestParam LocalDate checkOut) {
        return propertyService.isPropertyAvailable(id, checkIn, checkOut);
    }

    @GetMapping("/{id}/available-dates")
    public AvailableDatesResponse getAvailableDates(@PathVariable Integer id) {
        return new AvailableDatesResponse(propertyService.getAvailableDates(id));
    }

    private Property convertPropertyDTOToProperty(PropertyDTO propertyDTO) {
        return modelMapper.map(propertyDTO, Property.class);
    }

    private PropertyDTO convertPropertyToPropertyDTO(Property property) {
        return modelMapper.map(property, PropertyDTO.class);
    }

    @ExceptionHandler
    private ResponseEntity<Object> handleException(PropertyException e) {
        PropertyErrorResponse response = new PropertyErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

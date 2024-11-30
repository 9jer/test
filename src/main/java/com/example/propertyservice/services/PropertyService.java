package com.example.propertyservice.services;

import com.example.propertyservice.client.BookingClient;
import com.example.propertyservice.models.Property;
import com.example.propertyservice.models.PropertyFeature;
import com.example.propertyservice.repositories.PropertyFeatureRepository;
import com.example.propertyservice.repositories.PropertyRepository;
import com.example.propertyservice.util.PropertyException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final PropertyFeatureRepository propertyFeatureRepository;
    private final BookingClient bookingClient;

    public PropertyService(PropertyRepository propertyRepository, PropertyFeatureRepository propertyFeatureRepository, BookingClient bookingClient) {
        this.propertyRepository = propertyRepository;
        this.propertyFeatureRepository = propertyFeatureRepository;
        this.bookingClient = bookingClient;
    }

    public List<Property> findAll(){
        return propertyRepository.findAll();
    }

    public Property getPropertyById(Integer id){
        return propertyRepository.findById(id).orElseThrow(()->
                new PropertyException("Property not found"));
    }

    @Transactional
    public void save(Property property) {
        enrichPropertyForSave(property);
        property.setId(null);
        propertyRepository.save(property);
    }

    private void enrichPropertyForSave(Property property) {
        property.setFeatures(findOrCreatePropertyFeature(property));
        property.setCreatedAt(LocalDateTime.now());
    }

    @Transactional
    public void updatePropertyById(Integer id, Property updatedProperty) {
        // Найти существующее жилье
        Property existingProperty = getPropertyById(id);

        enrichPropertyForUpdate(existingProperty, updatedProperty);

        // Сохранить изменения
        propertyRepository.save(existingProperty);
    }

    private void enrichPropertyForUpdate(Property existingProperty, Property updatedProperty) {
        // Обновить основные поля
        existingProperty.setOwnerId(updatedProperty.getOwnerId());
        existingProperty.setTitle(updatedProperty.getTitle());
        existingProperty.setDescription(updatedProperty.getDescription());
        existingProperty.setLocation(updatedProperty.getLocation());
        existingProperty.setPricePerNight(updatedProperty.getPricePerNight());
        existingProperty.setCapacity(updatedProperty.getCapacity());

        // Найти или создать все характеристики
        Set<PropertyFeature> updatedFeatures = findOrCreatePropertyFeature(updatedProperty);

        // Очистить и обновить связи
        existingProperty.getFeatures().clear();
        existingProperty.getFeatures().addAll(updatedFeatures);

        // Установить время обновления
        existingProperty.setUpdatedAt(LocalDateTime.now());
    }

    public Boolean isPropertyAvailable(Integer propertyId, LocalDate checkIn, LocalDate checkOut) {
        return bookingClient.isPropertyAvailable(propertyId, checkIn, checkOut);
    }

    public List<LocalDate> getAvailableDates(Integer propertyId) {
        return bookingClient.getAvailableDates(propertyId);
    }

    public List<Property> search(String location, BigDecimal minPrice, BigDecimal maxPrice) {
        return propertyRepository.searchProperties(location, minPrice, maxPrice);
    }

    private Set<PropertyFeature> findOrCreatePropertyFeature(Property property) {
        return property.getFeatures().stream()
                .map(feature -> propertyFeatureRepository.findByName(feature.getName())
                        .orElseGet(() -> propertyFeatureRepository.save(feature)))
                .collect(Collectors.toSet());
    }

    @Transactional
    public void delete(Integer id) {
        propertyRepository.deleteById(id);
    }
}

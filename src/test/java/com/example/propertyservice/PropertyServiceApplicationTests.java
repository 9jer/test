package com.example.propertyservice;

import com.example.propertyservice.dto.PropertyDTO;
import com.example.propertyservice.models.Property;
import com.example.propertyservice.services.PropertyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PropertyServiceIntegrationTest {

    @Autowired
    private PropertyService propertyService;

    @Test
    void testSaveAndRetrieveProperty() {
        // Создаем тестовое свойство
        Property property = new Property();
        property.setOwnerId(1);
        property.setTitle("Test Property");
        property.setDescription("This is a test property");
        property.setLocation("123 Test Street");
        property.setPricePerNight(BigDecimal.valueOf(100.00));
        property.setCapacity(2);

        // Сохраняем свойство в базу данных
        propertyService.save(property);

        // Проверяем, что свойство сохранилось и его можно получить
        List<Property> properties = propertyService.findAll();
        assertThat(properties).hasSize(1);

        Property retrievedProperty = properties.get(0);
        assertThat(retrievedProperty.getTitle()).isEqualTo("Test Property");
        assertThat(retrievedProperty.getPricePerNight()).isEqualTo(BigDecimal.valueOf(100.00));
    }
}

package com.kamilzych.demo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamilzych.demo.model.Car;
import com.kamilzych.demo.model.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DemoTest {

    private static ObjectMapper mapper;

    @BeforeAll
    static void setup() {
        mapper = new ObjectMapper();

        // needed for missingVehicleJSON() to pass
        mapper.disable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);

        // needed for unknownVehicleType() to pass
        mapper.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
    }

    @Test
    void validJSON() throws JsonProcessingException {
        // works fine
        String json = "{ \"name\": \"kamil\", \"vehicleType\": \"CAR\", \"vehicle\": {\"wheels\": 4, \"color\": \"red\"}}";
        Person<?> person = mapper.readValue(json, Person.class);
        assertEquals("kamil", person.getName());
        assertEquals(Person.VehicleType.CAR, person.getVehicleType());
        assertNotNull(person.getVehicle());
        assertTrue(person.getVehicle() instanceof Car);
    }

    @Test
    void missingVehicleJSON() throws JsonProcessingException {
        // works fine, vehicle type is car but vehicle is null, as expected
        String json = "{ \"name\": \"kamil\", \"vehicleType\": \"CAR\"}";
        Person<?> person = mapper.readValue(json, Person.class);
        assertEquals("kamil", person.getName());
        assertEquals(Person.VehicleType.CAR, person.getVehicleType());
        assertNull(person.getVehicle());
    }

    @Test
    void unknownVehicleType() throws JsonProcessingException {
        // works fine, vehicle type does not match any enum value, both vehicle type and vehicle is null, as expected
        String json = "{ \"name\": \"kamil\", \"vehicleType\": \"PLANE\", \"vehicle\": {\"wheels\": 0}}";
        Person<?> person = mapper.readValue(json, Person.class);
        assertEquals("kamil", person.getName());
        assertNull(person.getVehicleType());
        assertNull(person.getVehicle());
    }

    @Test
    void missingVehicleTypeJSON() throws JsonProcessingException {
        // does not work, should behave the same way `unknownVehicleType` test does, but instead it throws
        // com.fasterxml.jackson.databind.exc.MismatchedInputException:
        // Unexpected token (END_OBJECT), expected VALUE_STRING: need JSON String that contains type id (for subtype of com.kamilzych.demo.model.Vehicle)
        String json = "{ \"name\": \"kamil\", \"vehicle\": {\"wheels\": 4, \"color\": \"red\"}}";
        Person<?> person = mapper.readValue(json, Person.class);
        assertEquals("kamil", person.getName());
        assertNull(person.getVehicleType());
        assertNull(person.getVehicle());
    }
}

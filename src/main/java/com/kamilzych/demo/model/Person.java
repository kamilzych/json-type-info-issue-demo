package com.kamilzych.demo.model;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

public class Person<T extends Vehicle> {
    private String name;
    private VehicleType vehicleType;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "vehicleType")
    @JsonTypeIdResolver(VehicleTypeResolver.class)
    private T vehicle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public T getVehicle() {
        return vehicle;
    }

    public void setVehicle(T vehicle) {
        this.vehicle = vehicle;
    }

    public enum VehicleType {
        CAR(Car.class),
        BICYCLE(Bicycle.class);

        private final Class<? extends Vehicle> vehicleClass;

        VehicleType(Class<? extends Vehicle> vehicleClass) {
            this.vehicleClass = vehicleClass;
        }
    }

    private static class VehicleTypeResolver extends TypeIdResolverBase {

        private JavaType superType;

        @Override
        public void init(JavaType bt) {
            this.superType = bt;
        }

        public String idFromValue(Object value) {
            return null;
        }

        public String idFromValueAndType(Object value, Class<?> suggestedType) {
            return null;
        }

        @Override
        public JavaType typeFromId(DatabindContext context, String id) throws IOException {
            try {
                Class<? extends Vehicle> vehicleClass = VehicleType.valueOf(id).vehicleClass;
                return context.constructSpecializedType(superType, vehicleClass);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        public JsonTypeInfo.Id getMechanism() {
            return JsonTypeInfo.Id.NAME;
        }
    }
}

package com.pmh.backendhomemedcare.exception;

public class GeocodingNotFoundException extends RuntimeException {
    public GeocodingNotFoundException(String message) {
        super(message);
    }
}

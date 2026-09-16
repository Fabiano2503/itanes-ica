package com.fabianoanticona.itanes.data.util;

/**
 * Helper class to validate coordinates.
 */
public class LocationValidator {

    /**
     * Validates if the given latitude and longitude are within valid ranges.
     * Latitude: -90 to 90
     * Longitude: -180 to 180
     *
     * @param latitude The latitude to validate
     * @param longitude The longitude to validate
     * @return true if both are valid, false otherwise
     */
    public static boolean isValid(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return false;
        }
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }
}

package ru.practicum.ewm.locations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationServiceImplTest {

    @Test
    void checkDistanceWhenCoordinatesAreEqualThenResult0() {
        float r0 = LocationService.distance(0f, 0f, 0f, 0f);
        assertEquals(r0, 0);

        float r1 = LocationService.distance(1.9f, 3.2f, 1.9f, 3.2f);
        assertEquals(r1, 0);
    }

    @Test
    void checkDistanceWhenCoordinatesDiffersBy1Deg() {
        float lat1 = 2;
        float lon1 = 2;
        float lat2 = 2;
        float lon2 = 3;
        float d;
        d = LocationService.distance(lat1, lon1, lat2, lon2);
        System.out.printf("Distance d[(%7.4f,%7.4f)(%7.4f,%7.4f)] = %f%n", lat1, lon1, lat2, lon2, d);
        assertTrue(d < 111.15f);

        lat1 = 89; lon1 = 1; lat2 = 90; lon2 = 1;
        d = LocationService.distance(lat1, lon1, lat2, lon2);
        System.out.printf("Distance d[(%7.4f,%7.4f)(%7.4f,%7.4f)] = %f%n", lat1, lon1, lat2, lon2, d);
        assertTrue(d < 111.15f);

        lat1 = 89; lon1 = 1; lat2 = 89; lon2 = 2;
        d = LocationService.distance(lat1, lon1, lat2, lon2);
        System.out.printf("Distance d[(%7.4f,%7.4f)(%7.4f,%7.4f)] = %f%n", lat1, lon1, lat2, lon2, d);
        assertTrue(d < 111.15f);

        lat1 = 89.5f; lon1 = 1; lat2 = 89.5f; lon2 = 2;
        d = LocationService.distance(lat1, lon1, lat2, lon2);
        System.out.printf("Distance d[(%7.4f,%7.4f)(%7.4f,%7.4f)] = %f%n", lat1, lon1, lat2, lon2, d);
        assertTrue(d < 111.15f);
    }

}
package com.m4l0n.hitchride.configuration;

// Programmer's Name: Ang Ru Xian
// Program Name: GoogleMapsConfig.java
// Description: This is a class that configures the Google Maps api
// Last Modified: 22 July 2023

import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleMapsConfig {

    @Value("${google.maps.apiKey}")
    private String GOOGLE_MAPS_API_KEY;

    @Bean
    public GeoApiContext buildGeoApiContext() {
        return new GeoApiContext.Builder()
                .apiKey(GOOGLE_MAPS_API_KEY)
                .build();
    }

}

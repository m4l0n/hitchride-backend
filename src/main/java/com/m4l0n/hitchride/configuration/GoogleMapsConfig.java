package com.m4l0n.hitchride.configuration;

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

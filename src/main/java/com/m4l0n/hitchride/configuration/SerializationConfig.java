package com.m4l0n.hitchride.configuration;

// Programmer's Name: Ang Ru Xian
// Program Name: SerializationConfig.java
// Description: This is a class that configures the serialization of certain objects when they are being sent to the client
// Last Modified: 22 July 2023

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.firestore.GeoPoint;
import com.m4l0n.hitchride.mapping.GeoPointDeserializer;
import com.m4l0n.hitchride.mapping.GeoPointSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SerializationConfig {

    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(GeoPoint.class, new GeoPointSerializer());
        simpleModule.addDeserializer(GeoPoint.class, new GeoPointDeserializer());
        mapper.registerModule(simpleModule);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

}

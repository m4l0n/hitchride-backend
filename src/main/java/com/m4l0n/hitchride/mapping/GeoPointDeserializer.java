package com.m4l0n.hitchride.mapping;

// Programmer's Name: Ang Ru Xian
// Program Name: GeoPointDeserializer.java
// Description: Custom deserializer for GeoPoint object
// Last Modified: 22 July 2023

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.cloud.firestore.GeoPoint;

import java.io.IOException;

public class GeoPointDeserializer extends JsonDeserializer<GeoPoint> {

    @Override
    public GeoPoint deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        double latitude = 0.0;
        double longitude = 0.0;

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jsonParser.getCurrentName();

            if ("latitude".equals(fieldName)) {
                jsonParser.nextToken();
                latitude = jsonParser.getDoubleValue();
            } else if ("longitude".equals(fieldName)) {
                jsonParser.nextToken();
                longitude = jsonParser.getDoubleValue();
            }
        }

        return new GeoPoint(latitude, longitude);
    }
}

package com.m4l0n.hitchride.mapping;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.cloud.firestore.GeoPoint;

import java.io.IOException;

public class GeoPointSerializer extends JsonSerializer<GeoPoint> {

    @Override
    public void serialize(GeoPoint geoPoint, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("latitude", geoPoint.getLatitude());
        jsonGenerator.writeNumberField("longitude", geoPoint.getLongitude());
        jsonGenerator.writeEndObject();
    }
}

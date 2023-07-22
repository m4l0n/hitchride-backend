package com.m4l0n.hitchride.utility;

// Programmer's Name: Ang Ru Xian
// Program Name: GoogleMapsApiClient.java
// Description: Client that calls the Google Maps API
// Last Modified: 22 July 2023

import com.google.cloud.firestore.GeoPoint;
import com.google.maps.*;
import com.google.maps.model.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class GoogleMapsApiClient {

    private final GeoApiContext geoApiContext;

    public GoogleMapsApiClient(GeoApiContext geoApiContext) {
        this.geoApiContext = geoApiContext;
    }

    public Map<String, String> getAddressFromCoordinates(GeoPoint coordinates) throws Exception {
        LatLng latLng = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
        GeocodingResult[] results = GeocodingApi.reverseGeocode(geoApiContext, latLng)
                .locationType(LocationType.ROOFTOP)
                .region("my")
                .language("en")
                .await();

        if (results.length > 0) {
            PlaceDetails placeDetails = getPlaceDetailsById(results[0].placeId);
            return Map.of(
                    "address", placeDetails.formattedAddress,
                    "name", placeDetails.name
            );
        }

        throw new Exception("Failed to get address from coordinates: " + coordinates);
    }

    public PlaceDetails getPlaceDetailsById(String placeId) throws Exception {
        try {
            PlaceDetailsRequest.FieldMask[] fieldMasks = new PlaceDetailsRequest.FieldMask[]{
                    PlaceDetailsRequest.FieldMask.FORMATTED_ADDRESS,
                    PlaceDetailsRequest.FieldMask.GEOMETRY,
                    PlaceDetailsRequest.FieldMask.NAME
            };
            PlaceDetailsRequest placeDetailsRequest = PlacesApi.placeDetails(geoApiContext, placeId)
                    .fields(fieldMasks);
            return placeDetailsRequest.await();
        } catch (InterruptedException | IOException e) {
            throw new Exception("Failed to get place details by ID: " + placeId, e);
        }
    }

    public CompletableFuture<Double> getDistance(String point1, String point2) {
        CompletableFuture<Double> future = new CompletableFuture<>();

        // The origin and destination are specified as place ids, therefore we need to add the prefix "place_id:"
        String origin = "place_id:" + point1;
        String destination = "place_id:" + point2;

        DistanceMatrixApi.newRequest(geoApiContext)
                .origins(origin)
                .destinations(destination)
                .setCallback(new PendingResult.Callback<>() { // Callback is used to make the call asynchronous
                    @Override
                    // This method is called when the API call is successful
                    public void onResult(DistanceMatrix result) {
                        if (result.rows.length > 0 && result.rows[0].elements.length > 0 &&
                                result.rows[0].elements[0].status == DistanceMatrixElementStatus.OK) {
                            future.complete(result.rows[0].elements[0].distance.inMeters / 1000.0); // Convert meters to kilometers
                        } else {
                            future.completeExceptionally(new Exception("Failed to calculate distance"));
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        future.completeExceptionally(e);
                    }
                });

        return future;
    }

}
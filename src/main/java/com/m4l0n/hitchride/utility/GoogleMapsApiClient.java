package com.m4l0n.hitchride.utility;

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

    private PlaceDetails getPlaceDetailsById(String placeId) throws Exception {
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

    public CompletableFuture<Double> getDistance(GeoPoint point1, GeoPoint point2) {
        CompletableFuture<Double> future = new CompletableFuture<>();
        LatLng origin = new LatLng(point1.getLatitude(), point1.getLongitude());
        LatLng destination = new LatLng(point2.getLatitude(), point2.getLongitude());

        DistanceMatrixApi.newRequest(geoApiContext)
                .origins(origin)
                .destinations(destination)
                .setCallback(new PendingResult.Callback<>() {
                    @Override
                    public void onResult(DistanceMatrix result) {
                        if (result.rows.length > 0 && result.rows[0].elements.length > 0 && result.rows[0].elements[0].status == DistanceMatrixElementStatus.OK) {
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
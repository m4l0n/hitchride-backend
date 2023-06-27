package com.m4l0n.hitchride.mapping;

import com.google.cloud.firestore.GeoPoint;
import com.m4l0n.hitchride.dto.UserDTO;
import com.m4l0n.hitchride.pojos.LocationData;
import com.m4l0n.hitchride.pojos.User;
import com.m4l0n.hitchride.utility.GoogleMapsApiClient;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserMapper implements BaseMapper<User, UserDTO> {

    private final GoogleMapsApiClient googleMapsApiClient;

    public UserMapper(GoogleMapsApiClient googleMapsApiClient) {
        this.googleMapsApiClient = googleMapsApiClient;
    }

    @Override
    public UserDTO mapPojoToDto(User pojo) {
        Map<String, LocationData> locationDataMap = pojo.getUserSavedLocations()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            try {
                                Map<String, String> stringMap = googleMapsApiClient.getAddressFromCoordinates(entry.getValue());
                                return new LocationData(stringMap.get("name"), stringMap.get("address"), entry.getValue());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                ));
        return new UserDTO(
                pojo.getUserId(),
                pojo.getUserName(),
                pojo.getUserEmail(),
                pojo.getUserPhoneNumber(),
                pojo.getUserPhotoUrl(),
                pojo.getUserPoints(),
                locationDataMap,
                pojo.getUserDriverInfo()
        );
    }

    @Override
    public User mapDtoToPojo(UserDTO dto) {
        Map<String, GeoPoint> geoPointMap = dto.userSavedLocations()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue()
                                .getAddressCoordinates()
                ));
        return new User(
                dto.userId(),
                dto.userName(),
                dto.userEmail(),
                dto.userPhoneNumber(),
                dto.userPhotoUrl(),
                dto.userPoints(),
                geoPointMap,
                dto.userDriverInfo()
        );
    }

}

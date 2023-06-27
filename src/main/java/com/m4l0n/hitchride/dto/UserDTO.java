package com.m4l0n.hitchride.dto;

import com.m4l0n.hitchride.pojos.DriverInfo;
import com.m4l0n.hitchride.pojos.LocationData;

import java.util.Map;

public record UserDTO(
        String userId,
        String userName,
        String userEmail,
        String userPhoneNumber,
        String userPhotoUrl,
        Integer userPoints,
        Map<String, LocationData> userSavedLocations,
        DriverInfo userDriverInfo
) {
}

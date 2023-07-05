package com.m4l0n.hitchride.service.validations;

import com.m4l0n.hitchride.pojos.DriverInfo;
import com.m4l0n.hitchride.pojos.HitchRideUser;

import java.time.LocalDate;

public class DriverInfoValidator {

    public String validateDriverInfoCreation(HitchRideUser driverUser) {
        StringBuilder error = new StringBuilder();

        this.validateDriverInfoCarAge(error, driverUser.getUserDriverInfo());
        this.validateCarSecondHand(error, driverUser.getUserDriverInfo());
        return error.toString();
    }

     private void validateDriverInfoCarAge(StringBuilder error, DriverInfo userDriverInfo) {
        if (userDriverInfo.getDiDateCarBoughtTimestamp() > System.currentTimeMillis()) {
            error.append("Car bought date cannot be in the future. ");
        }
        if (userDriverInfo.getDiDateCarBoughtTimestamp() < LocalDate.of(1900, 1, 1).toEpochDay()) {
            error.append("Car bought date cannot be before 1900. ");
        }
        if (System.currentTimeMillis() - userDriverInfo.getDiDateCarBoughtTimestamp() > 18 * 365 * 24 * 60 * 60) {
            error.append("Car age cannot be more than 18 years. ");
        }
     }

     private void validateCarSecondHand(StringBuilder error, DriverInfo userDriverInfo) {
        if (userDriverInfo.getDiIsCarSecondHand()) {
            error.append("Car cannot be second hand. ");
        }
     }

}

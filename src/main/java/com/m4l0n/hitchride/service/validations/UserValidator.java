package com.m4l0n.hitchride.service.validations;

import com.google.cloud.firestore.GeoPoint;
import com.m4l0n.hitchride.pojos.User;
import com.m4l0n.hitchride.utility.UtilityMethods;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class UserValidator {

    public String validateProfilePicture(MultipartFile imageFile) {
        StringBuilder errors = new StringBuilder();
        this.validateImageFileSize(errors, imageFile);
        this.validateImageFileExtension(errors, imageFile);

        return errors.toString();
    }

    public String validateSaveUserLocation(User currentUser, Map<String, GeoPoint> newSaveLocation) {
        StringBuilder errors = new StringBuilder();
        this.validateSaveLocationExists(errors, currentUser, newSaveLocation);

        return errors.toString();
    }

    private void validateImageFileSize(StringBuilder errors, MultipartFile imageFile) {
        if (imageFile.getSize() > 1000000)
            errors.append("File size is too large. ");
    }

    private void validateImageFileExtension(StringBuilder errors, MultipartFile imageFile) {
        if (!Objects.equals(imageFile.getContentType(), "image/jpeg") && !Objects.equals(imageFile.getContentType(), "image/png"))
            errors.append("File extension is not supported. ");
    }

    private void validateSaveLocationExists(StringBuilder errors, User currentUser, Map<String, GeoPoint> newSaveLocation) {
        Set<String> currentUserSavedLocations = currentUser.getUserSavedLocations()
                .keySet();
        Set<String> userNewSaveLocation = UtilityMethods.deepCopyMap(newSaveLocation)
                .keySet();
        userNewSaveLocation.retainAll(currentUserSavedLocations);

        if (!userNewSaveLocation.isEmpty())
            errors.append("Save location already exists. ");
    }
}

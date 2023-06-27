package com.m4l0n.hitchride.service.validations;

import com.m4l0n.hitchride.pojos.HitchRideUser;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

public class UserValidator {

    public String validateProfilePicture(MultipartFile imageFile) {
        StringBuilder errors = new StringBuilder();
        this.validateImageFileSize(errors, imageFile);
        this.validateImageFileExtension(errors, imageFile);

        return errors.toString();
    }

    public String validateUpdateProfile(HitchRideUser user, String currentLoggedInUser) {
        StringBuilder errors = new StringBuilder();

        this.validateUserID(errors, user.getUserId());
        this.validateValidUser(errors, user.getUserId(), currentLoggedInUser);

        return errors.toString();
    }

    public String validateCreateProfile(HitchRideUser user) {
        StringBuilder errors = new StringBuilder();

        this.validateUserID(errors, user.getUserId());

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

    private void validateValidUser(StringBuilder errors, String username, String currentLoggedInUser) {
        if (!Objects.equals(username, currentLoggedInUser))
            errors.append("Usernames do not match. ");
    }

    private void validateUserID(StringBuilder errors, String userID) {
        if (userID == null || userID.isEmpty())
            errors.append("User ID is empty. ");
    }

}

package com.m4l0n.hitchride.controller;

// Programmer's Name: Ang Ru Xian
// Program Name: UserController.java
// Description: This is a class that consists of all controller methods related to users
// Last Modified: 22 July 2023

import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.pojos.HitchRideUser;
import com.m4l0n.hitchride.response.Response;
import com.m4l0n.hitchride.response.ResponseAPI;
import com.m4l0n.hitchride.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public Response getProfile() {
        try {
            HitchRideUser user = userService.getProfile();

            return ResponseAPI.positiveResponse(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }

    @PostMapping("/create")
    public Response createUser(@RequestBody HitchRideUser user) {
        try {
            HitchRideUser createdUser = userService.createUser(user);

            if (createdUser == null) {
                throw new Exception("User already exists");
            }

            return ResponseAPI.positiveResponse(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }

    @PostMapping("/update")
    public Response updateUser(@RequestBody HitchRideUser user) {
        try {
            HitchRideUser updatedUser = userService.updateUser(user);

            return ResponseAPI.positiveResponse(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }

    @PostMapping("/profile-picture/upload")
    public Response uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        try {
            String profilePictureUrl = userService.updateUserProfilePicture(file);

            return ResponseAPI.positiveResponse(profilePictureUrl);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }

    @PostMapping("/updateDriverInfo")
    public Response updateDriverInfo(@RequestBody HitchRideUser user) {
        try {
            HitchRideUser updatedUser = userService.updateDriverInfo(user);

            return ResponseAPI.positiveResponse(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }


}

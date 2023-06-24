package com.m4l0n.hitchride.controller;

import com.google.cloud.firestore.GeoPoint;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.pojos.User;
import com.m4l0n.hitchride.response.Response;
import com.m4l0n.hitchride.response.ResponseAPI;
import com.m4l0n.hitchride.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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
            User user = userService.getProfile();

            return ResponseAPI.positiveResponse(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }

    @PostMapping("/create")
    public Response createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);

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
    public Response updateUser(@RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(user);

            return ResponseAPI.positiveResponse(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }

    @GetMapping("/getSavedLocations")
    public Response getUserSavedLocations() {
        try {
            Map<String, GeoPoint> userSavedLocations = userService.getUserSavedLocations();

            return ResponseAPI.positiveResponse(userSavedLocations);
        } catch (Exception e) {
            throw new HitchrideException(e.getMessage());
        }
    }
}

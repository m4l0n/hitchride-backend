package com.m4l0n.hitchride.controller;

import com.m4l0n.hitchride.dto.UserDTO;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.mapping.UserMapper;
import com.m4l0n.hitchride.pojos.User;
import com.m4l0n.hitchride.response.Response;
import com.m4l0n.hitchride.response.ResponseAPI;
import com.m4l0n.hitchride.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    public Response getProfile() {
        try {
            User user = userService.getProfile();

            return ResponseAPI.positiveResponse(userMapper.toDto(user));
        } catch (Exception e) {
            throw new HitchrideException(e.getMessage());
        }
    }

    @PostMapping("/create")
    public Response createUser(@RequestBody UserDTO userDTO) {
        try {
            User user = userMapper.toEntity(userDTO);
            User createdUser = userService.createUser(user);

            if (createdUser == null) {
                throw new Exception("User already exists");
            }

            return ResponseAPI.positiveResponse(userMapper.toDto(createdUser));
        } catch (Exception e) {
            throw new HitchrideException(e.getMessage());
        }
    }
}

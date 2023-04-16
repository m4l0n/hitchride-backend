package com.m4l0n.hitchride.controller;

import com.m4l0n.hitchride.mapping.UserMapper;
import com.m4l0n.hitchride.pojos.User;
import com.m4l0n.hitchride.response.Response;
import com.m4l0n.hitchride.response.ResponseAPI;
import com.m4l0n.hitchride.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    public Response getProfile(Principal principal) {
        User user = userService.getProfile();

        return ResponseAPI.positiveResponse(userMapper.toDto(user));
    }
}

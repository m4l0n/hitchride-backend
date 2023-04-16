package com.m4l0n.hitchride.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@RequestMapping("/api")
public class AppController {

    @GetMapping("/hello")
    public String hello(Principal principal) {
        return principal.getName();
    }

}

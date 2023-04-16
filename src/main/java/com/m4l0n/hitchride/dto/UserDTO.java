package com.m4l0n.hitchride.dto;

public record UserDTO(
        String id,
        String name,
        String email,
        String photoUrl,
        String phoneNumber,
        Integer points
) {

}

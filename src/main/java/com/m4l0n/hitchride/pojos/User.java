package com.m4l0n.hitchride.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    public String id;
    public String name;
    public String email;
    public String password;
    public String phoneNumber;
    public String photoUrl;
    public Integer points;
}

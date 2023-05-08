package com.m4l0n.hitchride.service;

import com.m4l0n.hitchride.pojos.User;

import java.util.concurrent.ExecutionException;

public interface UserService {

    User getProfile() throws ExecutionException, InterruptedException;

    User createUser(User user) throws ExecutionException, InterruptedException;

    User loadUserByUsername(String username) throws ExecutionException, InterruptedException;
}

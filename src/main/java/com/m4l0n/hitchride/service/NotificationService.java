package com.m4l0n.hitchride.service;

import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.concurrent.ExecutionException;

public interface NotificationService {

    String registerFcmToken(String fcmToken) throws InterruptedException, ExecutionException;

    String sendNotification(String targetUser, String title, String body, String type, String... payload) throws InterruptedException, ExecutionException, FirebaseMessagingException;

}

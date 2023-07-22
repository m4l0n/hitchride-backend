package com.m4l0n.hitchride.service;

// Programmer's Name: Ang Ru Xian
// Program Name: NotificationService.java
// Description: An interface that consists of methods to register an FCM token and send a notification
// Last Modified: 22 July 2023

import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.concurrent.ExecutionException;

public interface NotificationService {

    String registerFcmToken(String fcmToken) throws InterruptedException, ExecutionException;

    String sendNotification(String targetUser, String title, String body, String type, String... payload) throws InterruptedException, ExecutionException, FirebaseMessagingException;

}

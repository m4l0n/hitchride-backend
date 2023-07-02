package com.m4l0n.hitchride.service;

import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.concurrent.ExecutionException;

public interface NotificationService {

    String registerFcmToken(String fcmToken) throws InterruptedException, ExecutionException;

    void sendNotification(String fcmToken, String title, String body) throws InterruptedException, ExecutionException, FirebaseMessagingException;

}

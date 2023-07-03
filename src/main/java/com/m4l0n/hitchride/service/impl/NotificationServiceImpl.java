package com.m4l0n.hitchride.service.impl;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.m4l0n.hitchride.service.NotificationService;
import com.m4l0n.hitchride.service.shared.AuthenticationService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final AuthenticationService authenticationService;
    private final CollectionReference notificationCollection;

    public NotificationServiceImpl(AuthenticationService authenticationService, Firestore firestore) {
        this.authenticationService = authenticationService;
        this.notificationCollection = firestore.collection("fcm_token");
    }

    @Override
    public String registerFcmToken(String fcmToken) throws InterruptedException, ExecutionException {
        String userId = authenticationService.getAuthenticatedUsername();
        Map<String, String> data = Map.of("fcmToken", fcmToken, "userId", userId);
        notificationCollection.document()
                .set(data)
                .get();
        return userId;
    }

    @Override
    public String sendNotification(String targetUser, String title, String body, String type, String... payload) throws InterruptedException, ExecutionException, FirebaseMessagingException {
        String fcmToken = findFcmTokenByUserId(targetUser);
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Map<String, String> data = new java.util.HashMap<>(Map.of("type", type));
        if (payload.length > 0) {
            data.put("payload", payload[0]);
        }

        Message message = Message.builder()
                .setNotification(notification)
                .putAllData(data)
                .setToken(fcmToken)
                .build();

        return FirebaseMessaging.getInstance()
                .send(message);
    }

    private String findFcmTokenByUserId(String userId) throws ExecutionException, InterruptedException {
        return notificationCollection.whereEqualTo("userId", userId)
                .get()
                .get()
                .getDocuments()
                .get(0)
                .getString("fcmToken");
    }

}

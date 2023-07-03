package com.m4l0n.hitchride.controller;

import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.response.Response;
import com.m4l0n.hitchride.response.ResponseAPI;
import com.m4l0n.hitchride.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = "/notification", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/register")
    public Response registerFcmToken(@RequestBody Map<String, String> tokenMap) throws HitchrideException {
        try {
            String token = notificationService.registerFcmToken(tokenMap.get("token"));
            return ResponseAPI.positiveResponse(token);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }

    @PostMapping("/send")
    public Response sendNotification(@RequestBody Map<String, String> notificationMap) throws HitchrideException {
        try {
            String sentNotification = notificationService.sendNotification(notificationMap.get("targetUser"),
                    notificationMap.get("title"),
                    notificationMap.get("body"),
                    "common");
            return ResponseAPI.positiveResponse(sentNotification);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HitchrideException(e.getMessage());
        }
    }

}

package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.google.firebase.messaging.*;
import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.service.NotificationService;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void send(String title, String body, String messagingToken, @NotNull Map<String, String> data) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .putAllData(data)
                .setNotification(notification)
                .setToken(messagingToken)
                .build();

        FirebaseMessaging.getInstance().send(message);

    }

    @Override
    public void sendMultiple(String title, String body, List<String> messagingTokens, @NotNull Map<String, String> data) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .putAllData(data)
                .setNotification(notification)
                .addAllTokens(messagingTokens)
                .build();

        FirebaseMessaging.getInstance().sendMulticast(message);
    }
}

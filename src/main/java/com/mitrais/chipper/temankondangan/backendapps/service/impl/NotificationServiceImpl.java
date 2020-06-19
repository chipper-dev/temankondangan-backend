package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    private UserRepository userRepository;

    @Autowired
    public NotificationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void send(String title, String body, String messagingToken, @NotNull Map<String, String> data) {
        try{

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

        } catch (FirebaseMessagingException e) {
            throw new BadRequestException("Error: " + e);
        }
    }
}

package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.google.firebase.messaging.*;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.repository.ApplicantRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.NotificationRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void send(String title, String body, User user, @NotNull Map<String, String> data) {
        new Thread(() -> {
            try {
                Notification notification = Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build();

                saveNotificationData(title, body, user.getUserId());
                if(!StringUtils.isEmpty(user.getMessagingToken())) {
                    Message message = Message.builder()
                            .putAllData(data)
                            .setNotification(notification)
                            .setToken(user.getMessagingToken())
                            .build();

                    FirebaseMessaging.getInstance().send(message);
                }
            } catch (FirebaseMessagingException e) {

            }
        });
    }

    @Override
    public void sendMultiple(String title, String body, List<User> users, @NotNull Map<String, String> data) {
        new Thread(() -> {
            try {
                Notification notification = Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build();

                List<String> messagingTokens = new ArrayList<>();
                users.forEach(user -> {
                    if(!StringUtils.isEmpty(user.getMessagingToken())) {
                        messagingTokens.add(user.getMessagingToken());
                    }
                    saveNotificationData(title, body, user.getUserId());
                });

                if(!messagingTokens.isEmpty()) {
                    MulticastMessage message = MulticastMessage.builder()
                            .putAllData(data)
                            .setNotification(notification)
                            .addAllTokens(messagingTokens)
                            .build();

                    FirebaseMessaging.getInstance().sendMulticast(message);
                }
            } catch (FirebaseMessagingException e) {

            }
        });
    }

    @Override
    public List<com.mitrais.chipper.temankondangan.backendapps.model.Notification> getNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    private void saveNotificationData(String title, String body, Long userId) {
        com.mitrais.chipper.temankondangan.backendapps.model.Notification notification = new com.mitrais.chipper.temankondangan.backendapps.model.Notification();
        notification.setTitle(title);
        notification.setBody(body);
        notification.setUserId(userId);
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }
}

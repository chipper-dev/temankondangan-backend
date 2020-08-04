package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.google.firebase.messaging.*;
import com.google.gson.Gson;
import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.NotificationDataWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.NotificationRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
    public void send(String title, String body, User user, @NotNull Map<String, String> data) throws FirebaseMessagingException {
        data.putIfAbsent("click_action", "FLUTTER_NOTIFICATION_CLICK");
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        saveNotificationData(title, body, user.getUserId(), data);
        if(!StringUtils.isEmpty(user.getMessagingToken())) {
            Message message = Message.builder()
                    .putAllData(data)
                    .setNotification(notification)
                    .setToken(user.getMessagingToken())
                    .build();

            FirebaseMessaging.getInstance().send(message);
        }
    }

    @Override
    public void sendMultiple(String title, String body, List<User> users, @NotNull Map<String, String> data) throws FirebaseMessagingException {
        data.putIfAbsent("click_action", "FLUTTER_NOTIFICATION_CLICK");
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        List<String> messagingTokens = new ArrayList<>();
        users.forEach(user -> {
            if(!StringUtils.isEmpty(user.getMessagingToken())) {
                messagingTokens.add(user.getMessagingToken());
            }
            saveNotificationData(title, body, user.getUserId(), data);
        });

        if(!messagingTokens.isEmpty()) {
            MulticastMessage message = MulticastMessage.builder()
                    .putAllData(data)
                    .setNotification(notification)
                    .addAllTokens(messagingTokens)
                    .build();

            FirebaseMessaging.getInstance().sendMulticast(message);
        }
    }

    @Override
    public NotificationDataWrapper getNotifications(Long userId) {
        NotificationDataWrapper wrapper = new NotificationDataWrapper();
        Date thirtyDays = Timestamp.valueOf(LocalDateTime.now().minusDays(30));
        wrapper.setNotifications(notificationRepository.findByUserId(userId, thirtyDays));
        wrapper.setUnreadCount(notificationRepository.countUnreadByUserId(userId));
        return wrapper;
    }

    @Override
    public void setReadNotification(List<Long> notificationIds, Long userId) {
        if(notificationIds.isEmpty()) {
            notificationRepository.changeAllNotificationToReadByUserId(userId);
        } else {
            notificationIds.forEach(notificationId -> {
                com.mitrais.chipper.temankondangan.backendapps.model.Notification notification = notificationRepository.findById(notificationId).orElse(null);
                if(notification != null) {
                    if (notification.getUserId().equals(userId)) {
                        notification.setIsRead(true);
                        notificationRepository.save(notification);
                    } else {
                        throw new BadRequestException("Cannot set as read another User's notification.");
                    }
                }
            });
        }
    }

    private void saveNotificationData(String title, String body, Long userId, @NotNull Map<String, String> data) {
        com.mitrais.chipper.temankondangan.backendapps.model.Notification notification = new com.mitrais.chipper.temankondangan.backendapps.model.Notification();
        notification.setTitle(title);
        notification.setBody(body);
        notification.setUserId(userId);
        notification.setIsRead(false);

        Gson gson = new Gson();
        String jsonString = gson.toJson(data);
        notification.setData(jsonString);

        notificationRepository.save(notification);
    }
}

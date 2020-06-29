package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Notification;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.NotificationDataWrapper;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public interface NotificationService {

    void send(String title, String body, User user, @NotNull Map<String, String> data);

    void sendMultiple(String title, String body, List<User> Users, @NotNull Map<String, String> data);

    NotificationDataWrapper getNotifications(Long userId);

    void setReadNotification(List<Long> notificationIds, Long userId);
}

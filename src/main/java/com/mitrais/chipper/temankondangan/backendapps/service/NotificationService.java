package com.mitrais.chipper.temankondangan.backendapps.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.mitrais.chipper.temankondangan.backendapps.model.Notification;
import com.mitrais.chipper.temankondangan.backendapps.model.User;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public interface NotificationService {

    void send(String title, String body, User user, @NotNull Map<String, String> data);

    void sendMultiple(String title, String body, List<User> Users, @NotNull Map<String, String> data);

    List<Notification> getNotifications(Long userId);
}

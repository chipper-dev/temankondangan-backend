package com.mitrais.chipper.temankondangan.backendapps.service;

import com.google.firebase.messaging.FirebaseMessagingException;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public interface NotificationService {

    void send(String title, String body, String messagingToken, @NotNull Map<String, String> data) throws FirebaseMessagingException;

    void sendMultiple(String title, String body, List<String> messagingTokens, @NotNull Map<String, String> data) throws FirebaseMessagingException;
}

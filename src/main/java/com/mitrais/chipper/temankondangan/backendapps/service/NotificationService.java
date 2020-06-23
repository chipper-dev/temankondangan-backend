package com.mitrais.chipper.temankondangan.backendapps.service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public interface NotificationService {

    void send(String title, String body, String messagingToken, @NotNull Map<String, String> data);

    void sendMultiple(String title, String body, List<String> messagingTokens, @NotNull Map<String, String> data);
}

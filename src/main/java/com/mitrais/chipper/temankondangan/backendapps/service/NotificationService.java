package com.mitrais.chipper.temankondangan.backendapps.service;

import javax.validation.constraints.NotNull;
import java.util.Map;

public interface NotificationService {

    void send(String title, String body, String messagingToken, @NotNull Map<String, String> data);
}

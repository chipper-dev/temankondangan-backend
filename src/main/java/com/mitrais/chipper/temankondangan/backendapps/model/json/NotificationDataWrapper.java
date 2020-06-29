package com.mitrais.chipper.temankondangan.backendapps.model.json;

import com.mitrais.chipper.temankondangan.backendapps.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class NotificationDataWrapper {
    List<Notification> notifications;
    Integer unreadCount;
}

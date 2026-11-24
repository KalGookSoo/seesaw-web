package kr.me.seesaw.web.notification;

import kr.me.seesaw.api.notification.event.NotificationCreatedEvent;

public interface NotificationDeliveryService {

    void deliver(NotificationCreatedEvent event);

}

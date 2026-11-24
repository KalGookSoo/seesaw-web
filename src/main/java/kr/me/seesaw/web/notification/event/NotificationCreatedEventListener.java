package kr.me.seesaw.web.notification.event;

import kr.me.seesaw.api.notification.event.NotificationCreatedEvent;
import kr.me.seesaw.web.notification.NotificationDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class NotificationCreatedEventListener {

    private final NotificationDeliveryService notificationDeliveryService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNotificationCreated(NotificationCreatedEvent event) {
        notificationDeliveryService.deliver(event);
    }

}

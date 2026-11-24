package kr.me.seesaw.web.notification.event;

import kr.me.seesaw.api.notification.NotificationApplicationService;
import kr.me.seesaw.api.notification.event.ReportCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ReportCreatedEventListener {

    private final NotificationApplicationService notificationApplicationService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReportCreated(ReportCreatedEvent event) {
        notificationApplicationService.createForReport(event.siteId(), event.title(), event.content(), event.actorUsername(), event.sourceEventId());
    }

}

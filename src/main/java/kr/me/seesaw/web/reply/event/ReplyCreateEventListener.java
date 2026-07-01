package kr.me.seesaw.web.reply.event;

import kr.me.seesaw.api.reply.ReplyCreatedEvent;
import kr.me.seesaw.web.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReplyCreateEventListener {

    private final NotificationService notificationService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReplyCreated(ReplyCreatedEvent event) {
        notificationService.sendOnReplyCreated(event.articleId(), event.replyId(), event.content());
    }

}

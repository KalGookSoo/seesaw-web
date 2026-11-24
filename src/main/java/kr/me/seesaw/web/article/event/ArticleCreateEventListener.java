package kr.me.seesaw.web.article.event;

import kr.me.seesaw.api.article.event.ArticleCreatedEvent;
import kr.me.seesaw.api.notification.NotificationApplicationService;
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
public class ArticleCreateEventListener {

    private final NotificationApplicationService notificationApplicationService;

    private final NotificationService legacyNotificationService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onArticleCreated(ArticleCreatedEvent event) {
        notificationApplicationService.createForArticleCreated(event.categoryId(), event.articleId(), event.title(), event.content(), event.actorUsername(), event.sourceEventId());
        legacyNotificationService.sendOnArticleCreated(event.categoryId(), event.articleId(), event.title(), event.content());
    }

}

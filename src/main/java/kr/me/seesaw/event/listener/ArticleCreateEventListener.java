package kr.me.seesaw.event.listener;

import kr.me.seesaw.event.ArticleCreatedEvent;
import kr.me.seesaw.service.NotificationService;
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

    private final NotificationService notificationService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onArticleCreated(ArticleCreatedEvent event) {
        notificationService.sendOnArticleCreated(event.categoryId(), event.articleId(), event.title(), event.content());
    }

}

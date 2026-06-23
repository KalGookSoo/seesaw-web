package kr.me.seesaw.event.listener;

import kr.me.seesaw.event.ArticleCreatedEvent;
import kr.me.seesaw.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class ArticleCreateEventListener {

    private final NotificationService notificationService;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onArticleCreated(ArticleCreatedEvent event) {
        notificationService.sendOnArticleCreated(event.categoryId(), event.articleId(), event.title(), event.content());
    }

}

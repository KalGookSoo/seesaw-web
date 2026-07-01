package kr.me.seesaw.web.notification;

import kr.me.seesaw.core.domain.article.Article;

public interface WebPushNotificationService {

    void sendOnReplyCreated(Article article, String replyId, String content, String articleUrl);

}

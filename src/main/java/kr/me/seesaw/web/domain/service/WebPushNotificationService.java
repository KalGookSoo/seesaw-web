package kr.me.seesaw.web.domain.service;

import kr.me.seesaw.core.domain.article.Article;

public interface WebPushNotificationService {

    void sendOnReplyCreated(Article article, String replyId, String content, String articleUrl);

}

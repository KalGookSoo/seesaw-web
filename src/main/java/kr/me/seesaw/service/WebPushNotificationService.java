package kr.me.seesaw.service;

import kr.me.seesaw.domain.Article;

public interface WebPushNotificationService {

    void sendOnReplyCreated(Article article, String replyId, String content, String articleUrl);

}

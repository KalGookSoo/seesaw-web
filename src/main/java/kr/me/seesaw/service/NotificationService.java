package kr.me.seesaw.service;

public interface NotificationService {

    void sendOnArticleCreated(String articleId);

    void sendOnReplyCreated(String replyId);

}

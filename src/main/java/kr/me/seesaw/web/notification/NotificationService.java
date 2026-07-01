package kr.me.seesaw.web.notification;

public interface NotificationService {

    void sendOnArticleCreated(String categoryId, String articleId, String title, String content);

    void sendOnReplyCreated(String articleId, String replyId, String content);

}

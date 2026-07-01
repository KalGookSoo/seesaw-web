package kr.me.seesaw.web.notification;

public interface HelpdeskService {

    void sendToHelpdesk(String siteId, String title, String content);

}

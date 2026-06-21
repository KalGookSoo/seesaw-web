package kr.me.seesaw.service;

public interface MailService {

    void sendToReport(String siteId, String title, String content);

    void sendToHelpdesk(String siteId, String title, String content);

}

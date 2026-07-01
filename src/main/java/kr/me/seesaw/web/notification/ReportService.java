package kr.me.seesaw.web.notification;

public interface ReportService {

    void sendToReport(String siteId, String title, String content);

}

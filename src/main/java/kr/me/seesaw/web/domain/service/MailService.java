package kr.me.seesaw.web.domain.service;

import java.util.Map;

public interface MailService {

    void send(String from, String[] to, String title, String viewName, Map<String, String> values);

}

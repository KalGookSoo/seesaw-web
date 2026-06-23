package kr.me.seesaw.service;

import java.util.Map;

public interface MailService {

    void send(String from, String[] to, String title, String viewName, Map<String, String> values);

}

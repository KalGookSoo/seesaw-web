package kr.me.seesaw.service;

public interface NaverCalendarClient {

    String DEFAULT_CALENDAR_ID = "defaultCalendarId";

    void createSchedule(String scheduleIcalString);

    void updateSchedule(String scheduleIcalString);

    void deleteSchedule(String uid);

}

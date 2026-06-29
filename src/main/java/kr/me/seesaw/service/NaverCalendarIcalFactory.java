package kr.me.seesaw.service;

import kr.me.seesaw.domain.VEvent;
import kr.me.seesaw.domain.vo.RecurrenceRule;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class NaverCalendarIcalFactory {

    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    private static final DateTimeFormatter UTC_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
            .withZone(ZoneOffset.UTC);

    public String createScheduleIcalString(VEvent event) {
        return createScheduleIcalString(event, getDescription(event));
    }

    public String createScheduleIcalString(VEvent event, String description) {
        String tzid = StringUtils.hasText(event.getTzid()) ? event.getTzid() : "Asia/Seoul";
        String summary = StringUtils.hasText(event.getSummary()) ? event.getSummary() : event.getArticle().getTitle();
        return String.join("\n",
                        "BEGIN:VCALENDAR",
                        "VERSION:2.0",
                        "PRODID:Naver Calendar",
                        "CALSCALE:GREGORIAN",
                        "BEGIN:VTIMEZONE",
                        "TZID:" + tzid,
                        "BEGIN:STANDARD",
                        "DTSTART:19700101T000000",
                        "TZNAME:GMT+09:00",
                        "TZOFFSETFROM:+0900",
                        "TZOFFSETTO:+0900",
                        "END:STANDARD",
                        "END:VTIMEZONE",
                        "BEGIN:VEVENT",
                        "SEQUENCE:" + Optional.ofNullable(event.getSequence()).orElse(0),
                        "CLASS:PUBLIC",
                        "TRANSP:OPAQUE",
                        "UID:" + escape(event.getUid()),
                        "DTSTART;TZID=" + tzid + ":" + format(event.getDtStart()),
                        "DTEND;TZID=" + tzid + ":" + format(event.getDtEnd()),
                        "SUMMARY:" + escape(summary),
                        "DESCRIPTION:" + escape(toPlainText(description)),
                        "LOCATION:" + escape(event.getLocation()),
                        createRecurrenceRule(event.getRrule()),
                        createStatus(event),
                        createInstant("CREATED", event.getCreated()),
                        createInstant("LAST-MODIFIED", event.getLastModified()),
                        createInstant("DTSTAMP", event.getDtStamp()),
                        "END:VEVENT",
                        "END:VCALENDAR"
                ).lines()
                .filter(StringUtils::hasText)
                .toList()
                .stream()
                .reduce((left, right) -> left + "\n" + right)
                .orElse("");
    }

    private String getDescription(VEvent event) {
        if (event.getArticle() == null) {
            return null;
        }
        return event.getArticle().getContent();
    }

    private String createRecurrenceRule(RecurrenceRule rrule) {
        if (rrule == null || rrule.getFreq() == null) {
            return "";
        }

        List<String> parts = new ArrayList<>();
        parts.add("FREQ=" + rrule.getFreq().name());
        if (rrule.getInterval() != null) {
            parts.add("INTERVAL=" + rrule.getInterval());
        }
        if (rrule.getUntil() != null) {
            parts.add("UNTIL=" + format(rrule.getUntil()));
        }
        if (rrule.getCount() != null) {
            parts.add("COUNT=" + rrule.getCount());
        }
        if (StringUtils.hasText(rrule.getByDay())) {
            parts.add("BYDAY=" + rrule.getByDay());
        }
        if (StringUtils.hasText(rrule.getByMonth())) {
            parts.add("BYMONTH=" + rrule.getByMonth());
        }
        if (StringUtils.hasText(rrule.getByMonthDay())) {
            parts.add("BYMONTHDAY=" + rrule.getByMonthDay());
        }
        if (rrule.getWkst() != null) {
            parts.add("WKST=" + toIcalendarDay(rrule.getWkst()));
        }
        return "RRULE:" + String.join(";", parts);
    }

    private String createStatus(VEvent event) {
        return event.getStatus() == null ? "" : "STATUS:" + event.getStatus().name();
    }

    private String createInstant(String name, java.time.Instant instant) {
        return instant == null ? "" : name + ":" + UTC_DATE_TIME_FORMATTER.format(instant);
    }

    private String format(LocalDateTime dateTime) {
        return dateTime.format(LOCAL_DATE_TIME_FORMATTER);
    }

    private String toIcalendarDay(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case SUNDAY -> "SU";
            case MONDAY -> "MO";
            case TUESDAY -> "TU";
            case WEDNESDAY -> "WE";
            case THURSDAY -> "TH";
            case FRIDAY -> "FR";
            case SATURDAY -> "SA";
        };
    }

    private String toPlainText(String html) {
        return StringUtils.hasText(html) ? Jsoup.parse(html).text() : "";
    }

    private String escape(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace("\r\n", "\\n")
                .replace("\n", "\\n");
    }

}

package kr.me.seesaw.event.listener;

import kr.me.seesaw.domain.VEvent;
import kr.me.seesaw.event.VEventCreatedEvent;
import kr.me.seesaw.event.VEventDeletedEvent;
import kr.me.seesaw.event.VEventUpdatedEvent;
import kr.me.seesaw.repository.EventRepository;
import kr.me.seesaw.service.NaverCalendarClient;
import kr.me.seesaw.service.NaverCalendarIcalFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverCalendarEventListener {

    private final EventRepository eventRepository;

    private final NaverCalendarClient naverCalendarClient;

    private final NaverCalendarIcalFactory naverCalendarIcalFactory;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onVEventCreated(VEventCreatedEvent event) {
        try {
            VEvent vEvent = eventRepository.findById(event.eventId())
                    .orElseThrow(() -> new NoSuchElementException("네이버 캘린더 생성 대상 일정을 찾을 수 없습니다. eventId: " + event.eventId()));
            String scheduleIcalString = naverCalendarIcalFactory.createScheduleIcalString(vEvent);
            naverCalendarClient.createSchedule(scheduleIcalString);
        } catch (RuntimeException e) {
            log.warn("네이버 캘린더 일정 생성 연동에 실패했습니다. eventId={}", event.eventId(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onVEventUpdated(VEventUpdatedEvent event) {
        try {
            VEvent vEvent = eventRepository.findById(event.eventId())
                    .orElseThrow(() -> new NoSuchElementException("네이버 캘린더 수정 대상 일정을 찾을 수 없습니다. eventId: " + event.eventId()));
            String scheduleIcalString = naverCalendarIcalFactory.createScheduleIcalString(vEvent);
            naverCalendarClient.updateSchedule(scheduleIcalString);
        } catch (RuntimeException e) {
            log.warn("네이버 캘린더 일정 수정 연동에 실패했습니다. eventId={}", event.eventId(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onVEventDeleted(VEventDeletedEvent event) {
        try {
            naverCalendarClient.deleteSchedule(event.uid());
        } catch (RuntimeException e) {
            log.warn("네이버 캘린더 일정 삭제 연동에 실패했습니다. uid={}", event.uid(), e);
        }
    }

}

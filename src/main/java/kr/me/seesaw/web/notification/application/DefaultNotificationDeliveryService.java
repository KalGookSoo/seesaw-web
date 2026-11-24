package kr.me.seesaw.web.notification.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.me.seesaw.api.notification.MailService;
import kr.me.seesaw.api.notification.event.NotificationCreatedEvent;
import kr.me.seesaw.core.domain.notification.Notification;
import kr.me.seesaw.core.domain.notification.NotificationChannel;
import kr.me.seesaw.core.domain.notification.NotificationDelivery;
import kr.me.seesaw.core.domain.notification.NotificationDeliveryRepository;
import kr.me.seesaw.core.domain.notification.NotificationDeliveryStatus;
import kr.me.seesaw.core.domain.notification.NotificationPreference;
import kr.me.seesaw.core.domain.notification.NotificationPreferenceRepository;
import kr.me.seesaw.core.domain.notification.NotificationRepository;
import kr.me.seesaw.core.domain.notification.WebPushSubscription;
import kr.me.seesaw.core.domain.notification.WebPushSubscriptionRepository;
import kr.me.seesaw.core.domain.user.Email;
import kr.me.seesaw.core.domain.user.User;
import kr.me.seesaw.core.domain.user.UserRepository;
import kr.me.seesaw.web.notification.NotificationDeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class DefaultNotificationDeliveryService implements NotificationDeliveryService {

    private final NotificationRepository notificationRepository;

    private final NotificationPreferenceRepository preferenceRepository;

    private final NotificationDeliveryRepository deliveryRepository;

    private final WebPushSubscriptionRepository webPushSubscriptionRepository;

    private final UserRepository userRepository;

    private final PushService pushService;

    private final ObjectMapper objectMapper;

    private final MailService mailService;

    private final MailProperties mailProperties;

    @Override
    public void deliver(NotificationCreatedEvent event) {
        Notification notification = notificationRepository.findById(event.notificationId()).orElse(null);
        if (notification == null) {
            log.warn("전달할 알림을 찾을 수 없습니다. notificationId={}", event.notificationId());
            return;
        }

        markInAppDelivered(notification);
        NotificationPreference preference = preferenceRepository
                .findBySiteIdAndUserUsernameAndType(event.siteId(), event.recipientUsername(), event.type())
                .orElse(null);
        if (preference == null) {
            return;
        }
        if (preference.isWebPushEnabled()) {
            deliverWebPush(notification, event);
        }
        if (preference.isEmailEnabled()) {
            deliverEmail(notification, event);
        }
    }

    private void markInAppDelivered(Notification notification) {
        NotificationDelivery delivery = getOrCreate(notification, NotificationChannel.IN_APP);
        delivery.setStatus(NotificationDeliveryStatus.SENT);
        delivery.setDeliveredAt(LocalDateTime.now());
        deliveryRepository.save(delivery);
    }

    private void deliverWebPush(Notification notification, NotificationCreatedEvent event) {
        NotificationDelivery delivery = getOrCreate(notification, NotificationChannel.WEB_PUSH);
        List<WebPushSubscription> subscriptions = webPushSubscriptionRepository
                .findAllBySiteIdAndUserUsernameAndEnabledTrue(event.siteId(), event.recipientUsername());
        if (subscriptions.isEmpty()) {
            delivery.setStatus(NotificationDeliveryStatus.SKIPPED);
            delivery.setFailureReason("활성 Web Push 구독이 없습니다.");
            deliveryRepository.save(delivery);
            return;
        }

        String payload = createPushPayload(event);
        boolean delivered = false;
        for (WebPushSubscription subscription : subscriptions) {
            try {
                HttpResponse response = pushService.send(new nl.martijndwars.webpush.Notification(
                        subscription.getEndpoint(), subscription.getP256dh(), subscription.getAuth(), payload));
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode >= 200 && statusCode < 300) {
                    subscription.markUsed();
                    webPushSubscriptionRepository.save(subscription);
                    delivered = true;
                } else if (statusCode == 404 || statusCode == 410) {
                    subscription.disable();
                    webPushSubscriptionRepository.save(subscription);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Web Push 전달이 중단되었습니다. subscriptionId={}", subscription.getId(), e);
            } catch (Exception e) {
                log.warn("Web Push 전달에 실패했습니다. subscriptionId={}", subscription.getId(), e);
            }
        }
        delivery.setStatus(delivered ? NotificationDeliveryStatus.SENT : NotificationDeliveryStatus.FAILED);
        delivery.setDeliveredAt(delivered ? LocalDateTime.now() : null);
        deliveryRepository.save(delivery);
    }

    private void deliverEmail(Notification notification, NotificationCreatedEvent event) {
        NotificationDelivery delivery = getOrCreate(notification, NotificationChannel.EMAIL);
        User recipient = userRepository.findByUsername(event.recipientUsername()).orElse(null);
        if (recipient == null) {
            delivery.setStatus(NotificationDeliveryStatus.FAILED);
            delivery.setFailureReason("수신자 계정을 찾을 수 없습니다.");
            deliveryRepository.save(delivery);
            return;
        }
        Email email = recipient.getEmail();
        if (email.isEmpty()) {
            delivery.setStatus(NotificationDeliveryStatus.SKIPPED);
            delivery.setFailureReason("수신자 이메일이 없습니다.");
            deliveryRepository.save(delivery);
            return;
        }
        try {
            mailService.send(mailProperties.getUsername(), new String[]{email.toString()}, "[알림] " + event.title(),
                    "mail/notification", Map.of("title", event.title(), "message", event.message(), "targetUrl", event.targetUrl() == null ? "" : event.targetUrl()));
            delivery.setStatus(NotificationDeliveryStatus.SENT);
            delivery.setDeliveredAt(LocalDateTime.now());
        } catch (Exception e) {
            delivery.setStatus(NotificationDeliveryStatus.FAILED);
            delivery.setFailureReason(e.getMessage());
            log.warn("알림 이메일 전달에 실패했습니다. notificationId={}", notification.getId(), e);
        }
        deliveryRepository.save(delivery);
    }

    private NotificationDelivery getOrCreate(kr.me.seesaw.core.domain.notification.Notification notification,
                                             NotificationChannel channel) {
        return deliveryRepository.findByNotificationIdAndChannel(notification.getId(), channel)
                .orElseGet(() -> {
                    NotificationDelivery delivery = new NotificationDelivery();
                    delivery.setNotification(notification);
                    delivery.setChannel(channel);
                    return delivery;
                });
    }

    private String createPushPayload(NotificationCreatedEvent event) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "title", event.title(),
                    "body", event.message(),
                    "icon", "/favicon.png",
                    "badge", "/favicon.png",
                    "url", event.targetUrl() == null ? "" : event.targetUrl()
            ));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Web Push 페이로드를 생성할 수 없습니다.", e);
        }
    }

}

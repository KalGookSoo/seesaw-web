package kr.me.seesaw.web.notification.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.me.seesaw.web.notification.WebPushNotificationService;
import kr.me.seesaw.core.domain.article.Article;
import kr.me.seesaw.core.domain.notification.WebPushSubscription;
import kr.me.seesaw.core.domain.notification.WebPushSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DefaultWebPushNotificationService implements WebPushNotificationService {

    private final WebPushSubscriptionRepository webPushSubscriptionRepository;

    private final PushService pushService;

    private final ObjectMapper objectMapper;

    @Override
    public void sendOnReplyCreated(Article article, String replyId, String content, String articleUrl) {
        String username = article.getCreatedBy();
        if (!StringUtils.hasText(username)) {
            log.debug("게시글 작성자 정보가 없어 댓글 푸시 알림을 건너뜁니다. articleId={}", article.getId());
            return;
        }

        String siteId = article.getCategory().getSiteId();
        List<WebPushSubscription> subscriptions = webPushSubscriptionRepository.findAllBySiteIdAndUserUsernameAndEnabledTrue(siteId, username);
        if (subscriptions.isEmpty()) {
            log.debug("게시글 작성자의 활성 웹 푸시 구독이 없습니다. articleId={}, username={}", article.getId(), username);
            return;
        }

        try {
            String payload = createPayload(article, replyId, content, articleUrl);
            subscriptions.forEach(subscription -> send(pushService, subscription, payload));
        } catch (Exception e) {
            log.warn("댓글 웹 푸시 알림을 준비할 수 없습니다. articleId={}, replyId={}", article.getId(), replyId, e);
        }
    }

    private String createPayload(Article article, String replyId, String content, String articleUrl) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "title", "새 댓글이 등록되었습니다.",
                    "body", createBody(article, content),
                    "icon", "/favicon.png",
                    "badge", "/favicon.png",
                    "url", articleUrl,
                    "replyId", replyId
            ));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("웹 푸시 페이로드를 생성할 수 없습니다.", e);
        }
    }

    private String createBody(Article article, String content) {
        String title = StringUtils.hasText(article.getTitle()) ? article.getTitle() : "게시글";
        String safeContent = StringUtils.hasText(content) ? content : "댓글 내용이 없습니다.";
        String summary = safeContent.length() > 50 ? safeContent.substring(0, 50) + "..." : safeContent;
        return "%s: %s".formatted(title, summary);
    }

    private void send(PushService pushService, WebPushSubscription subscription, String payload) {
        try {
            Notification notification = new Notification(
                    subscription.getEndpoint(),
                    subscription.getP256dh(),
                    subscription.getAuth(),
                    payload
            );
            HttpResponse response = pushService.send(notification);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                subscription.markUsed();
                webPushSubscriptionRepository.save(subscription);
                return;
            }

            if (statusCode == 404 || statusCode == 410) {
                subscription.disable();
                webPushSubscriptionRepository.save(subscription);
            }
            log.warn("웹 푸시 전송에 실패했습니다. subscriptionId={}, statusCode={}", subscription.getId(), statusCode);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("웹 푸시 전송이 중단되었습니다. subscriptionId={}", subscription.getId(), e);
        } catch (Exception e) {
            log.warn("웹 푸시 전송 중 오류가 발생했습니다. subscriptionId={}", subscription.getId(), e);
        }
    }

}

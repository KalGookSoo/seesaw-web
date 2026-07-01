package kr.me.seesaw.web.notification;

import kr.me.seesaw.web.notification.dto.CreateWebPushSubscriptionRequest;
import kr.me.seesaw.web.notification.dto.DeleteWebPushSubscriptionRequest;
import kr.me.seesaw.web.notification.dto.WebPushSubscriptionResponse;

import java.util.List;

public interface WebPushSubscriptionService {

    WebPushSubscriptionResponse subscribe(CreateWebPushSubscriptionRequest command, String userAgent);

    void unsubscribe(DeleteWebPushSubscriptionRequest command);

    List<WebPushSubscriptionResponse> findAllEnabledBySiteId(String siteId);

}

package kr.me.seesaw.service;

import kr.me.seesaw.request.CreateWebPushSubscriptionRequest;
import kr.me.seesaw.request.DeleteWebPushSubscriptionRequest;
import kr.me.seesaw.response.WebPushSubscriptionResponse;

import java.util.List;

public interface WebPushSubscriptionService {

    WebPushSubscriptionResponse subscribe(CreateWebPushSubscriptionRequest command, String userAgent);

    void unsubscribe(DeleteWebPushSubscriptionRequest command);

    List<WebPushSubscriptionResponse> findAllEnabledBySiteId(String siteId);

}

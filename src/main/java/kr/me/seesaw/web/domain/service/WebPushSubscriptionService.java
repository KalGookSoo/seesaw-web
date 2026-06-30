package kr.me.seesaw.web.domain.service;

import kr.me.seesaw.api.dto.request.CreateWebPushSubscriptionRequest;
import kr.me.seesaw.api.dto.request.DeleteWebPushSubscriptionRequest;
import kr.me.seesaw.api.dto.response.WebPushSubscriptionResponse;

import java.util.List;

public interface WebPushSubscriptionService {

    WebPushSubscriptionResponse subscribe(CreateWebPushSubscriptionRequest command, String userAgent);

    void unsubscribe(DeleteWebPushSubscriptionRequest command);

    List<WebPushSubscriptionResponse> findAllEnabledBySiteId(String siteId);

}

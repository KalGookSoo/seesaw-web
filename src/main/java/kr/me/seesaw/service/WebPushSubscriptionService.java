package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateWebPushSubscriptionCommand;
import kr.me.seesaw.command.DeleteWebPushSubscriptionCommand;
import kr.me.seesaw.model.WebPushSubscriptionModel;

import java.util.List;

public interface WebPushSubscriptionService {

    WebPushSubscriptionModel subscribe(CreateWebPushSubscriptionCommand command, String userAgent);

    void unsubscribe(DeleteWebPushSubscriptionCommand command);

    List<WebPushSubscriptionModel> findAllEnabledBySiteId(String siteId);

}

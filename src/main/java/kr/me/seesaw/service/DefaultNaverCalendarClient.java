package kr.me.seesaw.service;

import jakarta.annotation.Resource;
import kr.me.seesaw.core.authentication.PrincipalProvider;
import kr.me.seesaw.dto.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultNaverCalendarClient implements NaverCalendarClient {

    private static final String CREATE_SCHEDULE_URL = "https://openapi.naver.com/calendar/createSchedule.json";

    private static final String UPDATE_SCHEDULE_URL = "https://openapi.naver.com/calendar/updateSchedule.json";

    private static final String DELETE_SCHEDULE_URL = "https://openapi.naver.com/calendar/deleteSchedule.json";

    private final OAuth2AuthorizedClientService clientService;

    private final PrincipalProvider principalProvider;

    @Resource(lookup = "naverCalendarRestTemplate")
    private final RestTemplate restTemplate;

    @Override
    public void createSchedule(String scheduleIcalString) {
        post(CREATE_SCHEDULE_URL, formData("scheduleIcalString", scheduleIcalString));
    }

    @Override
    public void updateSchedule(String scheduleIcalString) {
        post(UPDATE_SCHEDULE_URL, formData("scheduleIcalString", scheduleIcalString));
    }

    @Override
    public void deleteSchedule(String uid) {
        post(DELETE_SCHEDULE_URL, formData("uid", uid));
    }

    private void post(String url, MultiValueMap<String, String> formData) {
        final OAuth2Token accessToken = getAccessToken();
        formData.add("calendarId", DEFAULT_CALENDAR_ID);
        restTemplate.postForEntity(url, new HttpEntity<>(formData, headers(accessToken.getTokenValue())), String.class);
        log.debug("네이버 캘린더 API 호출 완료. url={}", url);
    }

    private MultiValueMap<String, String> formData(String key, String value) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(key, value);
        return formData;
    }

    private HttpHeaders headers(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private OAuth2Token getAccessToken() {
        final Authentication authentication = principalProvider.getAuthentication();
        final UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        final OAuth2AuthorizedClient client = clientService.loadAuthorizedClient("naver", principal.getUser().getEmail());
        return client.getAccessToken();
    }

}

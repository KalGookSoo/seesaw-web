package kr.me.seesaw.web.oauth2;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.Optional;

public class NaverUserDetail implements OAuth2UserDetail {

    private final Map<String, Object> attributes;

    public NaverUserDetail(Map<String, Object> attributes) {
        //noinspection unchecked
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @NonNull
    @Override
    public String getProviderId() {
        return this.attributes.get("id").toString();
    }

    @NonNull
    @Override
    public String getProvider() {
        return "naver";
    }

    @NonNull
    @Override
    public String getEmail() {
        return this.attributes.get("email").toString();
    }

    @Nullable
    @Override
    public String getName() {
        return Optional.ofNullable(this.attributes.get("name"))
                .map(Object::toString)
                .orElse(null);
    }

}

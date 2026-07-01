package kr.me.seesaw.web.oauth2;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface OAuth2UserDetail {

    @NonNull
    String getProviderId();

    @NonNull
    String getProvider();

    @NonNull
    String getEmail();

    @Nullable
    String getName();

}

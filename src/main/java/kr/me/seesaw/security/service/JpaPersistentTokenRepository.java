package kr.me.seesaw.security.service;

import kr.me.seesaw.security.entity.RememberMeToken;
import kr.me.seesaw.security.repository.RememberMeTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@RequiredArgsConstructor
public class JpaPersistentTokenRepository implements PersistentTokenRepository {

    private final RememberMeTokenRepository rememberMeTokenRepository;

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        RememberMeToken rememberMeToken = new RememberMeToken(
                token.getSeries(),
                token.getUsername(),
                token.getTokenValue(),
                token.getDate()
        );
        rememberMeTokenRepository.save(rememberMeToken);
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        RememberMeToken rememberMeToken = rememberMeTokenRepository.findBySeries(series)
                .orElseThrow(() -> new RememberMeAuthenticationException("No persistent token found for series id: " + series));
        rememberMeToken.update(tokenValue, lastUsed);
        rememberMeTokenRepository.save(rememberMeToken);
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        return rememberMeTokenRepository.findBySeries(seriesId)
                .map(token -> new PersistentRememberMeToken(token.getUsername(),
                        token.getSeries(),
                        token.getToken(),
                        token.getLastUsed()
                ))
                .orElse(null);
    }

    @Override
    @Transactional
    public void removeUserTokens(String username) {
        rememberMeTokenRepository.deleteByUsername(username);
    }
}

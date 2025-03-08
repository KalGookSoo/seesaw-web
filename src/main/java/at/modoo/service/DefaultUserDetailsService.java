package at.modoo.service;

import at.modoo.entity.UserPrincipal;
import at.modoo.repository.UserPrincipalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @see UserDetailsService
 */
@Service
@RequiredArgsConstructor
public class DefaultUserDetailsService implements UserDetailsService {

    private final UserPrincipalRepository userPrincipalRepository;

    /**
     * 계정명으로 사용자 인증 주체를 반환합니다.
     * @param username 계정명
     * @return 사용자 인증 주체
     * @throws UsernameNotFoundException 계정을 찾을 수 없는 예외
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userPrincipalRepository.findByUsername(username)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

}

package at.modoo.config;

import at.modoo.repository.UserPrincipalRepository;
import at.modoo.security.repository.RememberMeTokenRepository;
import at.modoo.security.service.JpaPersistentTokenRepository;
import at.modoo.service.DefaultUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String LOGIN_PAGE_PATH = "/sign-in";

    private static final String USERNAME_PARAM = "username";

    private static final String PASSWORD_PARAM = "password";

    private static final String LOGOUT_PATH = "/sign-out";

    private final UserPrincipalRepository userPrincipalRepository;

    private final RememberMeTokenRepository tokenRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return new DefaultUserDetailsService(userPrincipalRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        return new JpaPersistentTokenRepository(tokenRepository);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);

        return providerManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(this::hanbdleCsrfPolicies);
        http.cors(this::handleCorsPolicies);
        http.authorizeHttpRequests(this::handleAuthorizeHttpRequests);
        http.formLogin(this::handleFormLogin);
        http.userDetailsService(userDetailsService());
        http.logout(this::configureLogout);
        http.rememberMe(this::handleRememberMe);
        http.sessionManagement(this::handleSessionPolicies);

        return http.build();
    }

    private void hanbdleCsrfPolicies(CsrfConfigurer<HttpSecurity> config) {
        config.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }

    private void handleCorsPolicies(CorsConfigurer<HttpSecurity> config) {
        config.configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOriginPatterns(Collections.singletonList("/**"));
            configuration.setAllowedOrigins(Collections.singletonList(request.getHeader(HttpHeaders.ORIGIN)));
            configuration.setAllowedMethods(Arrays.asList(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name()));
            configuration.setAllowedHeaders(Arrays.asList(HttpHeaders.AUTHORIZATION, HttpHeaders.CACHE_CONTROL, HttpHeaders.CONTENT_TYPE, "X-XSRF-TOKEN"));
            configuration.setAllowCredentials(true);
            return configuration;
        });
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        PersistentTokenBasedRememberMeServices services = new PersistentTokenBasedRememberMeServices(
                UUID.randomUUID().toString(),
                userDetailsService(),
                persistentTokenRepository()
        );
        services.setAlwaysRemember(false);
        return services;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    private void handleAuthorizeHttpRequests(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry config) {
        // TODO /swagger resources 관련 ROLE_ADMIN만 접근할 수 있도록 할 것
        config.requestMatchers(new AntPathRequestMatcher("/managers/**")).hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(new AntPathRequestMatcher("/admins/**")).hasRole("ADMIN")
                .anyRequest()
                .permitAll();
    }

    private void handleFormLogin(FormLoginConfigurer<HttpSecurity> config) {
        config.loginPage(LOGIN_PAGE_PATH)
                .usernameParameter(USERNAME_PARAM)
                .passwordParameter(PASSWORD_PARAM)
                .successHandler(new SavedRequestAwareAuthenticationSuccessHandler())
                .failureHandler(this::handleBadCredentialsException);
    }

    private void handleBadCredentialsException(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        logger.error(exception.getMessage());
        response.sendRedirect(LOGIN_PAGE_PATH);
    }

    private void configureLogout(LogoutConfigurer<HttpSecurity> config) {
        config.logoutUrl(LOGOUT_PATH).permitAll();
    }

    private void handleRememberMe(RememberMeConfigurer<HttpSecurity> httpSecurityRememberMeConfigurer) {
        String key = UUID.randomUUID().toString();
        int tokenValiditySeconds = 60 * 60 * 24;
        httpSecurityRememberMeConfigurer.key(key)
                .rememberMeParameter("remember-me")
                .userDetailsService(userDetailsService())
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(tokenValiditySeconds);
    }

    private void handleSessionPolicies(SessionManagementConfigurer<HttpSecurity> config) {
        config.maximumSessions(1)
                .maxSessionsPreventsLogin(true)
                .expiredUrl(LOGIN_PAGE_PATH);
    }

}

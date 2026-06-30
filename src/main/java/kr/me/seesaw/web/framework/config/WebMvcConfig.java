package kr.me.seesaw.web.framework.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.me.seesaw.api.domain.context.SiteContext;
import kr.me.seesaw.interceptor.GlobalModelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final SiteContext siteContext;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(this.objectMapper()));
        WebMvcConfigurer.super.configureMessageConverters(converters);
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver("lang");
        localeResolver.setDefaultLocale(Locale.KOREAN);
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("LANGUAGE");
        return localeChangeInterceptor;
    }

    @Bean
    public GlobalModelInterceptor globalModelInterceptor() {
        return new GlobalModelInterceptor(siteContext);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/styles/**", "/scripts/**", "/images/**", "/fonts/**", "/favicon.png");

        registry.addInterceptor(globalModelInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/styles/**", "/scripts/**", "/images/**", "/fonts/**", "/favicon.png", "/api/**");
    }

    @Bean
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder
                .json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/sitemap.xml")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }

}

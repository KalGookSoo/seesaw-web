package kr.me.seesaw.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.me.seesaw.api.domain.context.SiteContext;
import kr.me.seesaw.api.dto.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RequiredArgsConstructor
public class GlobalModelInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SiteContext siteContext;

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, ModelAndView modelAndView) {
        if (modelAndView == null || modelAndView.getViewName() == null || modelAndView.getViewName().startsWith("redirect:")) {
            return;
        }

        logger.debug("요청 경로={}", request.getRequestURI());

        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromRequest(request);
        modelAndView.addObject("REQUEST_URI_BUILDER", uriBuilder);

        Map<String, CategoryResponse> allCategories = siteContext.getAllCategories();
        modelAndView.addObject("ALL_CATEGORIES", allCategories);
        modelAndView.addObject("NESTED_CATEGORIES", siteContext.getNestedCategories());
    }

}

package kr.me.seesaw.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.me.seesaw.context.SiteContext;
import kr.me.seesaw.model.CategoryModel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class GlobalModelInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Environment environment;

    private final SiteContext siteContext;

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, ModelAndView modelAndView) {
        if (modelAndView == null || modelAndView.getViewName() == null || modelAndView.getViewName().startsWith("redirect:")) {
            return;
        }

        logger.debug("요청 경로={}", request.getRequestURI());

        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromRequest(request);
        modelAndView.addObject("REQUEST_URI_BUILDER", uriBuilder);
        modelAndView.addObject("ACTIVE_PROFILES", List.of(environment.getActiveProfiles()));

        modelAndView.addObject("SITE_CONTEXT", siteContext.getSite());

        Map<String, CategoryModel> allCategories = siteContext.getAllCategories();
        modelAndView.addObject("ALL_CATEGORIES", allCategories);
        modelAndView.addObject("NESTED_CATEGORIES", siteContext.getNestedCategories());

        Optional.ofNullable(request.getParameter("categoryId"))
                .map(Objects::toString)
                .ifPresent(categoryId -> {
                    CategoryModel category = allCategories.get(categoryId);
                    modelAndView.addObject("CURRENT_CATEGORY", category);
                    logger.debug("현재 카테고리 속성 추가: categoryId={}, categoryName={}", categoryId, category != null ? category.getName() : "null");
                });
    }

}

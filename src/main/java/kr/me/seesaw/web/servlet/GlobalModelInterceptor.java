package kr.me.seesaw.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.me.seesaw.api.category.dto.CategoryResponse;
import kr.me.seesaw.api.site.SiteContext;
import kr.me.seesaw.api.site.dto.SiteResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GlobalModelInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SiteContext siteContext;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
        if (modelAndView == null || modelAndView.getViewName() == null || modelAndView.getViewName().startsWith("redirect:")) {
            return;
        }

        logger.debug("요청 경로={}", request.getRequestURI());

        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromRequest(request);
        modelAndView.addObject("REQUEST_URI_BUILDER", uriBuilder);

        final Map<String, Object> model = modelAndView.getModel();

        final SiteResponse site = getOrAdd(modelAndView, "SITE_CONTEXT", siteContext::getSiteContext);

        final Map<String, CategoryResponse> allCategories = getOrAdd(modelAndView, "ALL_CATEGORIES", () -> site.getCategories()
                .stream()
                .collect(Collectors.toMap(CategoryResponse::getId, Function.identity(), (oldValue, newValue) -> oldValue, LinkedHashMap::new)));

        getOrAdd(modelAndView, "NESTED_CATEGORIES", () -> siteContext.getNestedCategories(site.getCategories()));

        if (!model.containsKey("CURRENT_CATEGORY")) {
            Optional.ofNullable(request.getParameter("categoryId"))
                    .map(Objects::toString)
                    .ifPresent(categoryId -> {
                        CategoryResponse category = allCategories.get(categoryId);
                        modelAndView.addObject("CURRENT_CATEGORY", category);
                        logger.debug("현재 카테고리 속성 추가: categoryId={}, categoryName={}", categoryId, category != null ? category.getName() : "null");
                    });
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getOrAdd(ModelAndView modelAndView, String name, Supplier<T> supplier) {
        final Map<String, Object> model = modelAndView.getModel();
        if (model.containsKey(name)) {
            return (T) model.get(name);
        }
        final T value = supplier.get();
        modelAndView.addObject(name, value);
        return value;
    }

}

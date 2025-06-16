package kr.me.seesaw.interceptor;

import kr.me.seesaw.core.hierarchy.HierarchicalFactory;
import kr.me.seesaw.domain.Category;
import kr.me.seesaw.domain.Site;
import kr.me.seesaw.service.SiteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class NavigationInterceptor implements HandlerInterceptor {

    private final String domainName;

    private final SiteService siteService;
// TODO 요청 속성이 아닌 세션 속성으로 변경할 것.
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws Exception {
        try {

            // TODO 리팩토링할 것
            StringBuffer requestURL = request.getRequestURL();
            String queryString = request.getQueryString();
            if (queryString != null) {
                requestURL.append("?").append(queryString);
            }
            request.setAttribute(ContextEnvironment.REQUEST_URL, requestURL.toString());

            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String host = scheme + "://" + serverName;
            if (("http".equals(scheme) && serverPort != 80) || ("https".equals(scheme) && serverPort != 443)) {
                host += ":" + serverPort;
            }
            request.setAttribute(ContextEnvironment.REQUEST_HOST, host);

            String path = request.getRequestURI();
            request.setAttribute(ContextEnvironment.REQUEST_PATH, path);

            request.setAttribute(ContextEnvironment.REQUEST_QUERY_STRING, queryString != null ? queryString : "");


            Site site = siteService.getSiteContext(domainName);
            request.setAttribute(ContextEnvironment.SITE_CONTEXT, site);

            // 요소 탐색 편의를 위한 속성 할당
            Map<String, Category> allCategories = site.getCategories().stream()
                    .collect(Collectors.toMap(Category::getId, Function.identity(), (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            request.setAttribute(ContextEnvironment.ALL_CATEGORIES, allCategories);

            // 계층형 목록 출력을 위한 속성 할당
            List<Category> nestedCategories = HierarchicalFactory.build(site.getCategories());
            request.setAttribute(ContextEnvironment.NESTED_CATEGORIES, nestedCategories);

            // 요청 파라미터에 카테고리 식별자가 있을 경우 현재 카테고리 정보를 속성에 할당
            Optional.ofNullable(request.getParameter("categoryId")).map(Objects::toString).ifPresent(categoryId -> {
                request.setAttribute(ContextEnvironment.CURRENT_CATEGORY, allCategories.get(categoryId));
            });
        } catch (NoSuchElementException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }
        return true;
    }

}

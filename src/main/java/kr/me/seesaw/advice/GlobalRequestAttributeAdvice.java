package kr.me.seesaw.advice;

import jakarta.servlet.http.HttpServletRequest;
import kr.me.seesaw.core.hierarchy.HierarchicalFactory;
import kr.me.seesaw.interceptor.ContextEnvironment;
import kr.me.seesaw.model.CategoryModel;
import kr.me.seesaw.model.SiteModel;
import kr.me.seesaw.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ControllerAdvice(annotations = Controller.class)
public class GlobalRequestAttributeAdvice {

    private static final String REQUEST_URI_BUILDER = "REQUEST_URI_BUILDER";

    private final Environment environment;

    private final SiteService siteService;

    @ModelAttribute
    public void addAttributes(HttpServletRequest request, Model model) {
        String accept = request.getHeader(HttpHeaders.ACCEPT);
        if (accept != null && !accept.contains(MediaType.TEXT_HTML_VALUE)) {
            return;
        }

        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromRequest(request);
        model.addAttribute(REQUEST_URI_BUILDER, uriBuilder);

        model.addAttribute(ContextEnvironment.ACTIVE_PROFILES, List.of(environment.getActiveProfiles()));

        String applicationName = environment.getProperty("spring.application.name");
        String domainName = applicationName + ".seesaw.me.kr";
        SiteModel site = siteService.getSiteContext(domainName);
        model.addAttribute(ContextEnvironment.SITE_CONTEXT, site);

        // 요소 탐색 편의를 위한 속성 할당
        Map<String, CategoryModel> allCategories = site.getCategories().stream()
                .collect(Collectors.toMap(CategoryModel::getId, Function.identity(), (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        model.addAttribute(ContextEnvironment.ALL_CATEGORIES, allCategories);

        // 계층형 목록 출력을 위한 속성 할당
        List<CategoryModel> nestedCategories = HierarchicalFactory.build(site.getCategories());
        model.addAttribute(ContextEnvironment.NESTED_CATEGORIES, nestedCategories);

        // 요청 파라미터에 카테고리 식별자가 있을 경우 현재 카테고리 정보를 속성에 할당
        Optional.ofNullable(request.getParameter("categoryId"))
                .map(Objects::toString)
                .ifPresent(categoryId -> model.addAttribute(ContextEnvironment.CURRENT_CATEGORY, allCategories.get(categoryId)));
    }

}

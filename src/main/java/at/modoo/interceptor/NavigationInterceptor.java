package at.modoo.interceptor;

import at.modoo.core.hierarchy.HierarchicalFactory;
import at.modoo.model.Category;
import at.modoo.model.Site;
import at.modoo.repository.SiteRepository;
import at.modoo.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class NavigationInterceptor implements HandlerInterceptor {

    private final String domainName;

    private final SiteRepository siteRepository;

    private final CategoryService categoryService;

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws Exception {
        Optional<Site> site = siteRepository.findByDomainName(domainName);
        if (site.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }
        request.setAttribute(ContextEnvironment.SITE_CONTEXT, site.get());
        List<Category> categories = categoryService.findAll()
                .stream()
                .filter(Category::isPublic)
                .toList();

        List<Category> nestedCategories = HierarchicalFactory.build(categories);
        Map<String, Category> allCategories = categories.stream().collect(Collectors.toMap(Category::getId, Function.identity()));
        request.setAttribute(ContextEnvironment.NESTED_CATEGORIES, nestedCategories);
        request.setAttribute(ContextEnvironment.ALL_CATEGORIES, allCategories);

        Optional.ofNullable(request.getParameter("categoryId")).map(Objects::toString).ifPresent(categoryId -> {
            request.setAttribute(ContextEnvironment.CURRENT_CATEGORY, allCategories.get(categoryId));
        });

        return true;
    }

}

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
import java.util.Optional;

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
        request.setAttribute(ContextEnvironment.NESTED_CATEGORIES, HierarchicalFactory.build(categories));
        return true;
    }

}

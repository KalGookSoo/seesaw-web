package at.modoo.interceptor;

import at.modoo.core.hierarchy.HierarchicalFactory;
import at.modoo.model.Article;
import at.modoo.model.BaseEntity;
import at.modoo.model.Category;
import at.modoo.model.Site;
import at.modoo.repository.ArticleSearchRepository;
import at.modoo.repository.CategoryRepository;
import at.modoo.repository.SiteRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class NavigationInterceptor implements HandlerInterceptor {

    private final String domainName;

    private final SiteRepository siteRepository;

    private final CategoryRepository categoryRepository;

    private final ArticleSearchRepository articleSearchRepository;

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
        List<Category> categories = categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "sequence"))
                .stream()
                .filter(Category::isExposed)
                .toList();

        // 최근 7일 게시글 조회
        List<String> categoryIds = categories.stream().map(Category::getId).toList();
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
        List<Article> articles = articleSearchRepository.findAllByCategoryId(categoryIds, cutoffDate)
                .stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate))
                .toList();
        categories.forEach(category -> category.joinArticles(articles));

        Map<String, Category> allCategories = categories.stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));
        request.setAttribute(ContextEnvironment.ALL_CATEGORIES, allCategories);


        List<Category> nestedCategories = HierarchicalFactory.build(categories);

        // 리프 노드 카테고리의 게시글을 상위 노드에 바인딩함
        nestedCategories.forEach(category -> articles.stream()
                .filter(article -> category.getId().equals(allCategories.get(article.getCategoryId()).getParentId()))
                .forEach(category::addArticle));
        request.setAttribute(ContextEnvironment.NESTED_CATEGORIES, nestedCategories);


        Optional.ofNullable(request.getParameter("categoryId")).map(Objects::toString).ifPresent(categoryId -> {
            request.setAttribute(ContextEnvironment.CURRENT_CATEGORY, allCategories.get(categoryId));
        });

        return true;
    }

}

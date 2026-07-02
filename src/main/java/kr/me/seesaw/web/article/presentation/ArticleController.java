package kr.me.seesaw.web.article.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import kr.me.seesaw.api.article.ArticleContext;
import kr.me.seesaw.api.article.dto.ArticleResponse;
import kr.me.seesaw.api.article.dto.SearchArticlesRequest;
import kr.me.seesaw.api.calendar.dto.SearchEventsRequest;
import kr.me.seesaw.api.category.CategoryService;
import kr.me.seesaw.api.category.dto.CategoryResponse;
import kr.me.seesaw.core.support.pattern.PatternMatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RequiredArgsConstructor
@Validated
@Controller
@RequestMapping(value = "/articles", produces = MediaType.TEXT_HTML_VALUE)
public class ArticleController {

    private final CategoryService categoryService;

    private final ArticleContext articleContext;

    @GetMapping(params = "categoryType=STATIC_CONTENT")
    public String getArticles(
            @RequestParam @NotBlank @Pattern(regexp = PatternMatcher.UUID_V4) String categoryId,
            Model model
    ) {
        Sort sort = Sort.by(Sort.Order.asc("fixedOrder"), Sort.Order.desc("createdDate"));
        Pageable pageable = Pageable.unpaged(sort);
        Page<ArticleResponse> page = articleContext.findAllByCategoryId(categoryId, pageable);

        model.addAttribute("page", page);

        return "articles/static_content";
    }

    @GetMapping
    public String getArticlesInListView(
            @PageableDefault(size = 8, sort = "article.createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @Valid @ModelAttribute("search") SearchArticlesRequest search,
            Model model
    ) {
        CategoryResponse category = categoryService.getCategoryById(search.getCategoryId());
        if (search.getCategoryType() == null) {
            search.setCategoryType(category.getType());
        }
        // 공지는 오름차순 후 createdDate로 내림차순
        Sort sort = Sort.by(Sort.Order.asc("fixedOrder"), Sort.Order.desc("createdDate"));
        List<ArticleResponse> fixedArticles = articleContext.getFixedArticles(search.getCategoryId(), true, sort);

        Page<ArticleResponse> page = articleContext.findAll(pageable, search);

        model.addAttribute("fixedArticles", fixedArticles);
        model.addAttribute("page", page);

        return "articles/table";
    }

    @GetMapping(params = "categoryType=SCHEDULE")
    public String getArticlesInCalendarView(
            @Valid @ModelAttribute("search") SearchEventsRequest request,
            Model model
    ) {
        final String calendarSubscriptionUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/calendars/categories/{categoryId}/events.ics")
                .buildAndExpand(request.categoryId())
                .toUriString();
        model.addAttribute("calendarSubscriptionUrl", calendarSubscriptionUrl);

        return "events/index";
    }

    @GetMapping(params = {"viewType=CARD"})
    public String getArticlesInCardView(
            @PageableDefault(size = 9, sort = "article.createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @Valid @ModelAttribute("search") SearchArticlesRequest search,
            Model model
    ) {
        Page<ArticleResponse> page = articleContext.findAll(pageable, search);

        model.addAttribute("page", page);

        return "articles/card";
    }

    @GetMapping("/{id}")
    public String getArticle(
            @PathVariable("id") @NotBlank @Pattern(regexp = PatternMatcher.UUID_V4) String id,
            @Valid @ModelAttribute("search") SearchArticlesRequest search,
            Model model
    ) {
        ArticleResponse article = articleContext.getArticleAggregation(id);
        CategoryResponse category = categoryService.getCategoryById(search.getCategoryId());
        if (search.getCategoryId() == null) {
            search.setCategoryId(article.getCategoryId());
        }
        if (search.getCategoryType() == null) {
            search.setCategoryType(category.getType());
        }
        model.addAttribute("article", article);

        ArticleResponse previousArticle = articleContext.getPreviousArticle(search, article.getCreatedDate());
        model.addAttribute("previousArticle", previousArticle);

        ArticleResponse nextArticle = articleContext.getNextArticle(search, article.getCreatedDate());
        model.addAttribute("nextArticle", nextArticle);

        return "articles/view";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/new")
    public String getArticleNew(
            @ModelAttribute("search") SearchArticlesRequest search
    ) {
        return "articles/new";
    }

    @PreAuthorize("@articlePermissionContext.isOwner(#id, authentication.name)")
    @GetMapping("/{id}/edit")
    public String getArticleEdit(
            @PathVariable("id") @NotBlank @Pattern(regexp = PatternMatcher.UUID_V4) String id,
            @Valid @ModelAttribute("search") SearchArticlesRequest search,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        ArticleResponse article = articleContext.find(id);

        model.addAttribute("article", article);
        model.addAttribute("page", page);

        return "articles/edit";
    }

}

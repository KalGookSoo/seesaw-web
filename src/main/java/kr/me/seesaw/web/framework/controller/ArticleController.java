package kr.me.seesaw.web.framework.controller;

import kr.me.seesaw.api.domain.context.ArticleContext;
import kr.me.seesaw.api.domain.context.CategoryContext;
import kr.me.seesaw.api.dto.request.SearchEventsRequest;
import kr.me.seesaw.api.dto.response.ArticleResponse;
import kr.me.seesaw.api.dto.response.CategoryResponse;
import kr.me.seesaw.api.dto.request.search.SearchArticlesRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/articles", produces = MediaType.TEXT_HTML_VALUE)
public class ArticleController {

    private final CategoryContext categoryContext;

    private final ArticleContext articleContext;

    @GetMapping(params = "categoryType=STATIC_CONTENT")
    public String getArticles(
            @RequestParam String categoryId,
            Model model
    ) {
        CategoryResponse category = categoryContext.getCategory(categoryId);
        Sort sort = Sort.by(Sort.Order.asc("fixedOrder"), Sort.Order.desc("createdDate"));
        Pageable pageable = Pageable.unpaged(sort);
        Page<ArticleResponse> page = articleContext.findAllByCategoryId(categoryId, pageable);

        model.addAttribute("selectedCategory", category);
        model.addAttribute("page", page);

        return "articles/static_content";
    }

    @GetMapping
    public String getArticlesInListView(
            @PageableDefault(size = 8, sort = "article.createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute("search") SearchArticlesRequest search,
            Model model
    ) {
        CategoryResponse category = categoryContext.getCategory(search.getCategoryId());
        if (search.getCategoryType() == null) {
            search.setCategoryType(category.getType());
        }
        // 공지는 오름차순 후 createdDate로 내림차순
        Sort sort = Sort.by(Sort.Order.asc("fixedOrder"), Sort.Order.desc("createdDate"));
        List<ArticleResponse> fixedArticles = articleContext.getFixedArticles(search.getCategoryId(), true, sort);

        Page<ArticleResponse> page = articleContext.findAll(pageable, search);

        model.addAttribute("selectedCategory", category);
        model.addAttribute("fixedArticles", fixedArticles);
        model.addAttribute("page", page);

        return "articles/table";
    }

    @GetMapping(params = "categoryType=SCHEDULE")
    public String getArticlesInCalendarView(
            @ModelAttribute("search") SearchEventsRequest request,
            Model model
    ) {
        model.addAttribute("selectedCategory", categoryContext.getCategory(request.categoryId()));
        return "events/index";
    }

    @GetMapping(params = {"viewType=CARD"})
    public String getArticlesInCardView(
            @PageableDefault(size = 9, sort = "article.createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute("search") SearchArticlesRequest search,
            Model model
    ) {
        CategoryResponse category = categoryContext.getCategory(search.getCategoryId());
        Page<ArticleResponse> page = articleContext.findAll(pageable, search);

        model.addAttribute("selectedCategory", category);
        model.addAttribute("page", page);

        return "articles/card";
    }

    @GetMapping("/{id}")
    public String getArticle(
            @PathVariable("id") String id,
            @ModelAttribute("search") SearchArticlesRequest search,
            Model model
    ) {
        ArticleResponse article = articleContext.getArticleAggregation(id);
        CategoryResponse category = categoryContext.getCategory(article.getCategoryId());
        if (search.getCategoryId() == null) {
            search.setCategoryId(article.getCategoryId());
        }
        if (search.getCategoryType() == null) {
            search.setCategoryType(category.getType());
        }
        model.addAttribute("selectedCategory", category);
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
            @ModelAttribute("search") SearchArticlesRequest search,
            Model model
    ) {
        model.addAttribute("selectedCategory", categoryContext.getCategory(search.getCategoryId()));
        return "articles/new";
    }

    @PreAuthorize("@articlePermissionContext.isOwner(#id, authentication.name)")
    @GetMapping("/{id}/edit")
    public String getArticleEdit(
            @PathVariable("id") String id,
            @ModelAttribute("search") SearchArticlesRequest search,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        ArticleResponse article = articleContext.find(id);
        model.addAttribute("selectedCategory", categoryContext.getCategory(article.getCategoryId()));

        model.addAttribute("article", article);
        model.addAttribute("page", page);

        return "articles/edit";
    }

}

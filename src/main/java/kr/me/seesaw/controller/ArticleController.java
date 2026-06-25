package kr.me.seesaw.controller;

import kr.me.seesaw.context.ArticleContext;
import kr.me.seesaw.context.CategoryContext;
import kr.me.seesaw.dto.query.EventQuery;
import kr.me.seesaw.model.ArticleModel;
import kr.me.seesaw.model.CategoryModel;
import kr.me.seesaw.search.ArticleSearch;
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
        CategoryModel category = categoryContext.getCategory(categoryId);
        Sort sort = Sort.by(Sort.Order.asc("fixedOrder"), Sort.Order.desc("createdDate"));
        Pageable pageable = Pageable.unpaged(sort);
        Page<ArticleModel> page = articleContext.findAllByCategoryId(categoryId, pageable);

        model.addAttribute("selectedCategory", category);
        model.addAttribute("page", page);

        return "articles/static_content";
    }

    @GetMapping
    public String getArticlesInListView(
            @PageableDefault(size = 8, sort = "article.createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute("search") ArticleSearch search,
            Model model
    ) {
        CategoryModel category = categoryContext.getCategory(search.getCategoryId());
        if (search.getCategoryType() == null) {
            search.setCategoryType(category.getType());
        }
        // 공지는 오름차순 후 createdDate로 내림차순
        Sort sort = Sort.by(Sort.Order.asc("fixedOrder"), Sort.Order.desc("createdDate"));
        List<ArticleModel> fixedArticles = articleContext.getFixedArticles(search.getCategoryId(), true, sort);

        Page<ArticleModel> page = articleContext.findAll(pageable, search);

        model.addAttribute("selectedCategory", category);
        model.addAttribute("fixedArticles", fixedArticles);
        model.addAttribute("page", page);

        return "articles/table";
    }

    @GetMapping(params = "categoryType=SCHEDULE")
    public String getArticlesInCalendarView(
            @ModelAttribute("search") EventQuery search,
            Model model
    ) {
        model.addAttribute("selectedCategory", categoryContext.getCategory(search.getCategoryId()));
        return "events/index";
    }

    @GetMapping(params = {"viewType=CARD"})
    public String getArticlesInCardView(
            @PageableDefault(size = 9, sort = "article.createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute("search") ArticleSearch search,
            Model model
    ) {
        CategoryModel category = categoryContext.getCategory(search.getCategoryId());
        Page<ArticleModel> page = articleContext.findAll(pageable, search);

        model.addAttribute("selectedCategory", category);
        model.addAttribute("page", page);

        return "articles/card";
    }

    @GetMapping("/{id}")
    public String getArticle(
            @PathVariable("id") String id,
            @ModelAttribute("search") ArticleSearch search,
            Model model
    ) {
        ArticleModel article = articleContext.getArticleAggregation(id);
        CategoryModel category = categoryContext.getCategory(article.getCategoryId());
        if (search.getCategoryId() == null) {
            search.setCategoryId(article.getCategoryId());
        }
        if (search.getCategoryType() == null) {
            search.setCategoryType(category.getType());
        }
        model.addAttribute("selectedCategory", category);
        model.addAttribute("article", article);

        ArticleModel previousArticle = articleContext.getPreviousArticle(search, article.getCreatedDate());
        model.addAttribute("previousArticle", previousArticle);

        ArticleModel nextArticle = articleContext.getNextArticle(search, article.getCreatedDate());
        model.addAttribute("nextArticle", nextArticle);

        return "articles/view";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/new")
    public String getArticleNew(
            @ModelAttribute("search") ArticleSearch search,
            Model model
    ) {
        model.addAttribute("selectedCategory", categoryContext.getCategory(search.getCategoryId()));
        return "articles/new";
    }

    @PreAuthorize("@articlePermissionContext.isOwner(#id, authentication.name)")
    @GetMapping("/{id}/edit")
    public String getArticleEdit(
            @PathVariable("id") String id,
            @ModelAttribute("search") ArticleSearch search,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        ArticleModel article = articleContext.find(id);
        model.addAttribute("selectedCategory", categoryContext.getCategory(article.getCategoryId()));

        model.addAttribute("article", article);
        model.addAttribute("page", page);

        return "articles/edit";
    }

}

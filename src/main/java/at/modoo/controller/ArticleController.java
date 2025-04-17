package at.modoo.controller;

import at.modoo.command.CreateArticleCommand;
import at.modoo.command.UpdateArticleCommand;
import at.modoo.model.Article;
import at.modoo.search.ArticleSearch;
import at.modoo.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping(params = "categoryType=STATIC_CONTENT")
    public String getArticles(
            @RequestParam String categoryId,
            @PageableDefault(size = 1, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        Page<Article> page = articleService.findAllByCategoryId(categoryId, pageable);

        model.addAttribute("page", page);

        return "articles/static_content";
    }

    @GetMapping(params = "categoryType=BOARD")
    public String getArticlesInListView(
            @PageableDefault(size = 8, sort = "article.createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute("search") ArticleSearch search,
            Model model
    ) {
        // 공지는 오름차순 후 createdDate로 내림차순
        Sort sort = Sort.by(Sort.Order.asc("fixedOrder"), Sort.Order.desc("createdDate"));
        List<Article> fixedArticles = articleService.getFixedArticles(search.getCategoryId(), true, sort);

        // 비공지
        Page<Article> page = articleService.findAll(pageable, search);

        model.addAttribute("fixedArticles", fixedArticles);
        model.addAttribute("page", page);

        return "articles/table";
    }

    @GetMapping(params = {"categoryType=BOARD", "viewType=CARD"})
    public String getArticlesInCardView(
            @PageableDefault(size = 9, sort = "article.createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute("search") ArticleSearch search,
            Model model
    ) {
        Page<Article> page = articleService.findAll(pageable, search);

        model.addAttribute("page", page);

        return "articles/card";
    }

    @GetMapping("/view")
    public String getArticle(
            @ModelAttribute("search") ArticleSearch search,
            @PageableDefault(size = 1, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        Page<Article> page = articleService.findAll(pageable, search);

        model.addAttribute("page", page);

        return "articles/view";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/new")
    public String getArticleNew(@ModelAttribute("command") CreateArticleCommand command) {
        return "articles/new";
    }

    @PreAuthorize("@defaultArticleService.isOwner(#id, authentication.name)")
    @GetMapping("/{id}/edit")
    public String getArticleEdit(
            @PathVariable("id") String id,
            @ModelAttribute("command") UpdateArticleCommand command,
            Model model
    ) {
        Article article = articleService.find(id);

        model.addAttribute("article", article);

        return "articles/edit";
    }

}

package at.modoo.controller;

import at.modoo.model.Article;
import at.modoo.model.Category;
import at.modoo.search.ArticleSearch;
import at.modoo.service.ArticleService;
import at.modoo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final CategoryService categoryService;

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
        Page<Article> page = articleService.findAll(pageable, search);

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

    @GetMapping("/{id}")
    public String getArticle(
            @PathVariable("id") String id,
            @RequestParam String categoryId,
            @PageableDefault(size = 1, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        Page<Article> page = articleService.view(categoryId, pageable);

        model.addAttribute("page", page);

        return "articles/view";
    }

    @GetMapping("/{id}/edit")
    public String getArticleEdit(
            @PathVariable("id") String id,
            @RequestParam String categoryId,
            Model model
    ) {
        Category category = categoryService.find(categoryId);
        Article article = articleService.find(id);

        model.addAttribute("category", category);
        model.addAttribute("article", article);

        return "articles/edit";
    }

}

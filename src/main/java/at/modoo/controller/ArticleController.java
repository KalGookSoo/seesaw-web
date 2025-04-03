package at.modoo.controller;

import at.modoo.model.Article;
import at.modoo.model.Category;
import at.modoo.search.ArticleSearch;
import at.modoo.service.ArticleService;
import at.modoo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public String getArticles(Model model) {
        // Query


        // Model

        // View
        return "articles/static_content";
    }

    @GetMapping(params = "categoryType=BOARD")
    public String getArticles(
            @PageableDefault Pageable pageable,
            @ModelAttribute ArticleSearch search,
            Model model
    ) {
        // Query
        Category category = categoryService.find(search.getCategoryId());
        Page<Article> page = articleService.findAll(pageable, search);

        // Model
        model.addAttribute("category", category);
        model.addAttribute("page", page);

        // View
        return "articles/board";
    }

}

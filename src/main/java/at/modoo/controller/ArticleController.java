package at.modoo.controller;

import at.modoo.model.Article;
import at.modoo.model.Category;
import at.modoo.search.ArticleSearch;
import at.modoo.service.ArticleService;
import at.modoo.service.CategoryService;
import lombok.Getter;
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
    public String getArticles(Model model) {
        // Query


        // Model

        // View
        return "articles/static_content";
    }

    @GetMapping(params = "categoryType=BOARD")
    public String getArticles(
            @PageableDefault(size = 8, sort = "article.createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute("search") ArticleSearch search,
            @RequestParam(required = false, defaultValue = "LIST") String viewType,
            Model model
    ) {
        Category category = categoryService.find(search.getCategoryId());
        Page<Article> page = articleService.findAll(pageable, search);

        model.addAttribute("currentCategory", category);
        model.addAttribute("page", page);

        return "articles/" + ViewTypeResolver.valueOf(viewType).getViewName();
    }

    @GetMapping("/{id}")
    public String getArticle(
            @PathVariable("id") String id,
            @PageableDefault(size = 1, page = 0) Pageable pageable,
            Model model
    ) {
        return null;
    }

    @Getter
    @RequiredArgsConstructor
    enum ViewTypeResolver {
        LIST("board"),
        CARD("card");
        private final String viewName;
    }

}

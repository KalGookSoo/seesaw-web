package at.modoo;

import at.modoo.model.Article;
import at.modoo.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 메인 컨트롤러
 */
@RequiredArgsConstructor
@Controller
public class IndexController {

    private final ArticleService articleService;

    /**
     * 메인 화면을 반환합니다.
     *
     * @return 메인 화면
     */
    @GetMapping("/")
    public String index(
            @PageableDefault(size = 3, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        // 모임활동 > 2025책꽂이, 알림장 > 공지사항 top 3 게시
        String firstCategoryId = "0236be97-9c8f-4ada-a120-039f540fabee";
        Page<Article> first = articleService.findAllByCategoryId(firstCategoryId, pageable);
        String secondCategoryId = "70cdb7f0-17f9-45e7-a872-914a968c09b6";
        Page<Article> second = articleService.findAllByCategoryId(secondCategoryId, pageable);

        model.addAttribute("first", first);
        model.addAttribute("firstCategoryId", firstCategoryId);
        model.addAttribute("second", second);
        model.addAttribute("secondCategoryId", secondCategoryId);

        return "index";
    }

}
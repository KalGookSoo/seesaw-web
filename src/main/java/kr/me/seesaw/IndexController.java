package kr.me.seesaw;

import kr.me.seesaw.context.CurrentSiteContext;
import kr.me.seesaw.domain.vo.CategoryType;
import kr.me.seesaw.dto.model.VEventModel;
import kr.me.seesaw.dto.query.EventQuery;
import kr.me.seesaw.model.ArticleModel;
import kr.me.seesaw.model.CategoryModel;
import kr.me.seesaw.service.ArticleService;
import kr.me.seesaw.service.EventWebService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 메인 컨트롤러
 */
@RequiredArgsConstructor
@Controller
@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
public class IndexController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ArticleService articleService;

    private final EventWebService eventWebService;

    private final CurrentSiteContext currentSiteContext;

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

        logger.debug("하위 카테고리 중 사이트 노출 카테고리 목록을 추출");
        Map<String, CategoryModel> allCategories = currentSiteContext.getAllCategories();
        List<String> siteExposedBoardCategoryIds = allCategories != null ? allCategories.values()
                .stream()
                .filter(Predicate.not(CategoryModel::isRoot))
                .filter(CategoryModel::isSiteExposed)
                .filter(category -> category.getType().equals(CategoryType.BOARD))
                .sorted(Comparator.comparing(CategoryModel::getSiteExposedOrder))
                .map(CategoryModel::getId)
                .toList() : Collections.emptyList();

        logger.debug("사이트 노출 게시글은 최근 3 개의 게시글로 규정");
        Map<String, Page<ArticleModel>> siteExposedBoardPages = siteExposedBoardCategoryIds.stream()
                .collect(Collectors.toMap(Function.identity(),
                        id -> articleService.findAllByCategoryId(id, pageable),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        model.addAttribute("siteExposedBoardPages", siteExposedBoardPages);

        logger.debug("스케쥴 타입 카테고리 중 사이트 노출 카테고리 목록을 추출");
        List<String> siteExposedScheduleCategoryIds = allCategories != null ? allCategories.values()
                .stream()
                .filter(Predicate.not(CategoryModel::isRoot))
                .filter(CategoryModel::isSiteExposed)
                .filter(category -> category.getType().equals(CategoryType.SCHEDULE))
                .sorted(Comparator.comparing(CategoryModel::getSiteExposedOrder))
                .map(CategoryModel::getId)
                .toList() : Collections.emptyList();

        logger.debug("사이트 노출 일정은 이번 달 기준으로 규정");
        LocalDate now = LocalDate.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

        Map<String, List<VEventModel>> siteExposedScheduleEvents = siteExposedScheduleCategoryIds.stream()
                .collect(Collectors.toMap(Function.identity(),
                        id -> {
                            EventQuery query = new EventQuery();
                            query.setCategoryId(id);
                            query.setStart(startOfMonth);
                            query.setEnd(endOfMonth);
                            return eventWebService.findAll(query);
                        },
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        model.addAttribute("siteExposedScheduleEvents", siteExposedScheduleEvents);

        return "index";
    }

}

package kr.me.seesaw.web;

import kr.me.seesaw.api.article.ArticleService;
import kr.me.seesaw.api.article.dto.ArticleResponse;
import kr.me.seesaw.api.calendar.EventWebService;
import kr.me.seesaw.api.calendar.dto.SearchEventsRequest;
import kr.me.seesaw.api.calendar.dto.VEventResponse;
import kr.me.seesaw.api.category.dto.CategoryResponse;
import kr.me.seesaw.api.site.SiteContext;
import kr.me.seesaw.api.site.dto.SiteResponse;
import kr.me.seesaw.core.domain.category.CategoryType;
import kr.me.seesaw.core.domain.event.EventStatus;
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

    private final SiteContext siteContext;

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
        final SiteResponse site = siteContext.getSiteContext();
        model.addAttribute("SITE_CONTEXT", site);

        final Map<String, CategoryResponse> allCategories = site.getCategories()
                .stream()
                .collect(Collectors.toMap(CategoryResponse::getId, Function.identity(), (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        model.addAttribute("ALL_CATEGORIES", allCategories);
        model.addAttribute("NESTED_CATEGORIES", siteContext.getNestedCategories(site.getCategories()));

        final List<String> siteExposedBoardCategoryIds = allCategories.values()
                .stream()
                .filter(Predicate.not(CategoryResponse::isRoot))
                .filter(CategoryResponse::isSiteExposed)
                .filter(category -> category.getType().equals(CategoryType.BOARD))
                .sorted(Comparator.comparing(CategoryResponse::getSiteExposedOrder))
                .map(CategoryResponse::getId)
                .toList();

        logger.debug("사이트 노출 게시글은 최근 3 개의 게시글로 규정");
        Map<String, Page<ArticleResponse>> siteExposedBoardPages = siteExposedBoardCategoryIds.stream()
                .collect(Collectors.toMap(Function.identity(),
                        id -> articleService.findAllByCategoryId(id, pageable),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        model.addAttribute("siteExposedBoardPages", siteExposedBoardPages);

        logger.debug("스케쥴 타입 카테고리 중 사이트 노출 카테고리 목록을 추출");
        final List<String> siteExposedScheduleCategoryIds = allCategories.values()
                .stream()
                .filter(Predicate.not(CategoryResponse::isRoot))
                .filter(CategoryResponse::isSiteExposed)
                .filter(category -> category.getType().equals(CategoryType.SCHEDULE))
                .sorted(Comparator.comparing(CategoryResponse::getSiteExposedOrder))
                .map(CategoryResponse::getId)
                .toList();

        logger.debug("사이트 노출 일정은 이번 달 기준으로 규정");
        final LocalDate now = LocalDate.now();
        final LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        final LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

        final Map<String, List<VEventResponse>> siteExposedScheduleEvents = siteExposedScheduleCategoryIds.stream()
                .collect(Collectors.toMap(Function.identity(),
                        id -> {
                            final SearchEventsRequest request = SearchEventsRequest.builder()
                                    .categoryId(id)
                                    .start(startOfMonth)
                                    .end(endOfMonth)
                                    .build();
                            return eventWebService.findAll(request);
                        },
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        model.addAttribute("siteExposedScheduleEvents", siteExposedScheduleEvents);

        model.addAttribute("eventStatus", Arrays.stream(EventStatus.values()).collect(Collectors.toMap(EventStatus::name, EventStatus::getDescription)));

        return "index";
    }

}

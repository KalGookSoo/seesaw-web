package kr.me.seesaw.context;

import kr.me.seesaw.core.hierarchy.HierarchicalFactory;
import kr.me.seesaw.domain.Category;
import kr.me.seesaw.domain.Site;
import kr.me.seesaw.model.ArticleModel;
import kr.me.seesaw.model.AttachmentModel;
import kr.me.seesaw.model.BaseModel;
import kr.me.seesaw.model.CategoryModel;
import kr.me.seesaw.model.SiteModel;
import kr.me.seesaw.repository.ArticleQueryRepository;
import kr.me.seesaw.repository.AttachmentRepository;
import kr.me.seesaw.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.RequestScope;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequestScope
@Transactional
@RequiredArgsConstructor
@Service
public class CurrentSiteContext {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SiteRepository siteRepository;

    private final AttachmentRepository attachmentRepository;

    private final ArticleQueryRepository articleQueryRepository;

    private final Environment environment;

    private SiteModel site;

    private Map<String, CategoryModel> allCategories;

    private List<CategoryModel> nestedCategories;

    @Transactional(readOnly = true)
    public SiteModel getSite() {
        if (this.site == null) {
            String applicationName = environment.getProperty("spring.application.name");
            String domainName = applicationName + ".seesaw.me.kr";
            this.site = getSiteContext(domainName);
        }
        return this.site;
    }

    private SiteModel getSiteContext(String domainName) {
        logger.debug("사이트 컨텍스트 조회: domainName={}", domainName);
        Site siteEntity = siteRepository.findByDomainName(domainName)
                .orElseThrow(() -> new NoSuchElementException("사이트를 찾을 수 없습니다. domainName: " + domainName));
        SiteModel siteModel = new SiteModel(siteEntity);

        logger.debug("프로필 이미지, 배경 이미지 조인");
        attachmentRepository.findAllByReferenceIdIn(Collections.singletonList(siteEntity.getId()))
                .stream()
                .map(AttachmentModel::new)
                .forEach(siteModel::addAttachment);

        logger.debug("카테고리 조인");
        siteEntity.getCategories()
                .stream()
                .filter(Category::isExposed)
                .sorted(Comparator.comparing(Category::getSequence))
                .map(CategoryModel::new)
                .forEach(siteModel::addCategory);

        logger.debug("최근 7일 게시글 조인");
        List<String> categoryIds = siteModel.getCategories()
                .stream()
                .map(CategoryModel::getId)
                .toList();
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);

        List<ArticleModel> articles = articleQueryRepository.findAllByCategoryId(categoryIds, cutoffDate)
                .stream()
                .map(ArticleModel::new)
                .sorted(Comparator.comparing(BaseModel::getCreatedDate))
                .toList();
        siteModel.getCategories()
                .forEach(categoryModel -> categoryModel.joinArticles(articles));

        Map<String, CategoryModel> allCategories = siteModel.getCategories()
                .stream()
                .collect(Collectors.toMap(CategoryModel::getId, Function.identity()));

        logger.debug("최근 게시글을 병합하여 상위 카테고리 게시글에 바인딩");
        siteModel.getCategories()
                .stream()
                .filter(CategoryModel::isRoot)
                .forEach(categoryModel -> articles.stream()
                        .filter(article -> categoryModel.getId().equals(allCategories.get(article.getCategoryId()).getParentId()))
                        .forEach(categoryModel::addRecentArticle));

        logger.debug("최근 게시글을 해당 카테고리에도 바인딩");
        articles.forEach(article -> {
            CategoryModel category = allCategories.get(article.getCategoryId());
            if (category != null) {
                category.addRecentArticle(article);
            }
        });

        return siteModel;
    }

    public Map<String, CategoryModel> getAllCategories() {
        if (this.allCategories == null) {
            this.allCategories = getSite().getCategories().stream()
                    .collect(Collectors.toMap(CategoryModel::getId, Function.identity(), (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        }
        return this.allCategories;
    }

    public List<CategoryModel> getNestedCategories() {
        if (this.nestedCategories == null) {
            this.nestedCategories = HierarchicalFactory.build(getSite().getCategories());
        }
        return this.nestedCategories;
    }

}

package at.modoo.service;

import at.modoo.model.Article;
import at.modoo.model.BaseEntity;
import at.modoo.model.Category;
import at.modoo.model.Site;
import at.modoo.repository.ArticleSearchRepository;
import at.modoo.repository.AttachmentRepository;
import at.modoo.repository.CategoryRepository;
import at.modoo.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class DefaultSiteService implements SiteService {

    private final SiteRepository siteRepository;

    private final AttachmentRepository attachmentRepository;

    private final CategoryRepository categoryRepository;

    private final ArticleSearchRepository articleSearchRepository;

    @Override
    public Site getSite(String domainName) {
        Site site = siteRepository.findByDomainName(domainName)
                .orElseThrow(NoSuchElementException::new);

        // 프로필 이미지, 배경 이미지 조인
        attachmentRepository.findAllByReferenceIdIn(Collections.singletonList(site.getId()))
                .forEach(site::addAttachment);

        // 카테고리 조인
        categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "sequence"))
                .stream()
                .filter(Category::isExposed)
                .forEach(site::addCategory);

        // 최근 7일 게시글 조인
        List<String> categoryIds = site.getCategories().stream().map(Category::getId).toList();
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
        List<Article> articles = articleSearchRepository.findAllByCategoryId(categoryIds, cutoffDate)
                .stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate))
                .toList();
        site.getCategories().forEach(category -> category.joinArticles(articles));

        Map<String, Category> allCategories = site.getCategories().stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        // 최근 게시글을 병합하여 상위 카테고리 게시글에 바인딩
        site.getCategories()
                .stream()
                .filter(Category::isRoot)
                .forEach(category -> articles.stream()
                        .filter(article -> category.getId().equals(allCategories.get(article.getCategoryId()).getParentId()))
                        .forEach(category::addArticle));

        return site;
    }
}

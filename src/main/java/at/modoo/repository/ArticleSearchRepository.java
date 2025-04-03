package at.modoo.repository;

import at.modoo.model.Article;
import at.modoo.search.ArticleSearch;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ArticleSearchRepository implements SearchRepository<Article, ArticleSearch> {

    private final EntityManager em;

    @Override
    public Page<Article> search(Pageable pageable, ArticleSearch search) {
        String jpql = "select article from Article article left join article.attachments attachments where 1 = 1";
        jpql += generateJpql(search);

        if (pageable.getSort().isSorted()) {
            jpql += " order by " + pageable.getSort().toString().replace(":", "");
        }

        TypedQuery<Article> query = em.createQuery(jpql, Article.class);
        setParameters(query, search);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // 결과 리스트 조회
        List<Article> articles = query.getResultList();

        // 카운트 쿼리
        String countJpql = "select count(distinct article) from Article article left join article.attachments attachments where 1 = 1";
        countJpql += generateJpql(search);

        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);
        setParameters(countQuery, search);

        Long count = countQuery.getSingleResult();

        return new PageImpl<>(articles, pageable, count);
    }

    private String generateJpql(@Nonnull ArticleSearch search) {
        StringBuilder jpql = new StringBuilder();
        if (StringUtils.hasText(search.getCategoryId())) {
            jpql.append(" and article.category.id = :categoryId");
        }
        if (StringUtils.hasText(search.getTitle())) {
            jpql.append(" and article.title like :title");
        }
        if (StringUtils.hasText(search.getContent())) {
            jpql.append(" and article.content like :content");
        }
        return jpql.toString();
    }

    private void setParameters(@Nonnull TypedQuery<?> query, @Nonnull ArticleSearch search) {
        if (StringUtils.hasText(search.getCategoryId())) {
            query.setParameter("categoryId", search.getCategoryId());
        }
        if (StringUtils.hasText(search.getTitle())) {
            query.setParameter("title", "%" + search.getTitle() + "%");
        }
        if (StringUtils.hasText(search.getContent())) {
            query.setParameter("content", "%" + search.getContent() + "%");
        }
    }

}

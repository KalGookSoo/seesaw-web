package at.modoo.repository;

import at.modoo.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, String> {
    Page<Article> findAllByCategoryId(String categoryId, Pageable pageable);
    List<Article> findAllByCategoryIdAndFixed(String categoryId, boolean fixed, Sort sort);
}

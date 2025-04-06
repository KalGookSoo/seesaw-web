package at.modoo.repository;

import at.modoo.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, String> {
    Page<Article> findAllByCategoryId(String categoryId, Pageable pageable);
}

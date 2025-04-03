package at.modoo.search;

import at.modoo.model.vo.CategoryType;
import lombok.Data;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Data
public class ArticleSearch {

    private String categoryId;

    private CategoryType categoryType;

    private String title;

    private String content;

    public UriComponentsBuilder getUriComponentsBuilder() {
        return UriComponentsBuilder
                .newInstance()
                .queryParamIfPresent("categoryId", Optional.ofNullable(categoryId))
                .queryParamIfPresent("title", Optional.ofNullable(title))
                .queryParamIfPresent("content", Optional.ofNullable(content));
    }

}

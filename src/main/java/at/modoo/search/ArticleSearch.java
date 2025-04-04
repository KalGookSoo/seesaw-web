package at.modoo.search;

import at.modoo.model.vo.CategoryType;
import lombok.Data;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Data
public class ArticleSearch implements UriComponentsProvider {

    private String categoryId;

    private CategoryType categoryType;

    private String title;

    private String content;

    @Override
    public UriComponentsBuilder getUriComponentsBuilder() {
        return UriComponentsProvider.super.getUriComponentsBuilder()
                .queryParamIfPresent("categoryId", Optional.ofNullable(categoryId))
                .queryParamIfPresent("categoryType", Optional.ofNullable(categoryType))
                .queryParamIfPresent("title", Optional.ofNullable(title))
                .queryParamIfPresent("content", Optional.ofNullable(content));
    }
}

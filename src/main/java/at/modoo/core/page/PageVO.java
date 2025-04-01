package at.modoo.core.page;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;

@Getter
@Setter
public abstract class PageVO implements Serializable {

    @Parameter(description = "페이지 인덱스", schema = @Schema(defaultValue = "0"))
    @Schema(description = "페이지 인덱스", defaultValue = "0")
    private int page = 0;

    @Parameter(description = "페이지 당 출력할 레코드 수", schema = @Schema(defaultValue = "10"))
    @Schema(description = "페이지 당 출력할 레코드 수", defaultValue = "10")
    private int size = 10;

    @Parameter(description = "정렬할 필드", schema = @Schema(defaultValue = "createdDate"))
    @Schema(description = "정렬할 필드", defaultValue = "createdDate")
    private String sort = "createdDate";

    @Parameter(description = "정렬 방향", schema = @Schema(defaultValue = "desc"))
    @Schema(description = "정렬 방향", defaultValue = "desc")
    private String sortDirection = "desc";

    public Pageable pageable() {
        return PageRequest.of(page, size, sort());
    }

    public Sort sort() {
        return Sort.by(Sort.Direction.fromString(sortDirection), sort);
    }

    protected UriComponentsBuilder getUriComponentsBuilder() {
        return UriComponentsBuilder.newInstance()
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort)
                .queryParam("sortDirection", sortDirection);
    }

}

package at.modoo.command;

import at.modoo.model.vo.ArticleType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "게시글 생성 커맨드")
@Data
public class CreateArticleCommand {

    @Parameter(description = "카테고리 식별자", required = true)
    @Schema(description = "카테고리 식별자", example = "카테고리 식별자")
    @NotBlank
    @NotNull
    private String categoryId;

    @Parameter(description = "타입", required = true)
    @Schema(description = "타입", example = "타입")
    @NotBlank
    @NotNull
    private ArticleType type;

    @Parameter(description = "고정여부", required = true)
    @Schema(description = "고정여부", example = "고정여부")
    private boolean fixed;

    @Parameter(description = "고정순서", required = true)
    @Schema(description = "고정순서", example = "고정순서")
    private Integer fixedOrder;

    @Parameter(description = "제목", required = true)
    @Schema(description = "제목", example = "제목")
    @NotBlank
    @NotNull
    private String title;

    @Parameter(description = "본문", required = true)
    @Schema(description = "본문", example = "본문")
    @NotBlank
    @NotNull
    private String content;

    @Parameter(description = "첨부파일")
    @Schema(description = "첨부파일", example = "첨부파일")
    private List<MultipartFile> multipartFiles = new ArrayList<>();

}

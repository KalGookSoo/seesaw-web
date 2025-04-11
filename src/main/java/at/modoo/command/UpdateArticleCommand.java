package at.modoo.command;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "게시글 수정 커맨드")
@Data
public class UpdateArticleCommand implements Serializable {

    @Parameter(description = "카테고리 식별자", required = true)
    @Schema(description = "카테고리 식별자", example = "카테고리 식별자")
    @NotBlank
    @NotNull
    private String categoryId;

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

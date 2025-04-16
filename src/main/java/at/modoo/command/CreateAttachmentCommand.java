package at.modoo.command;

import at.modoo.model.Attachment;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "첨부파일 생성 커맨드")
@Data
public class CreateAttachmentCommand {

    @Parameter(description = "참조 식별자", required = true)
    @Schema(description = "참조 식별자", example = "참조 식별자")
    @NotBlank
    @NotNull
    private String referencedId;

    @Parameter(description = "타입", required = true)
    @Schema(description = "타입", example = "타입")
    @NotNull
    private Attachment.Type type;

    @Parameter(description = "첨부파일")
    @Schema(description = "첨부파일", example = "첨부파일")
    private MultipartFile multipartFile;

}

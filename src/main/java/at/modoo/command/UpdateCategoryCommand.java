
package at.modoo.command;

import at.modoo.entity.CategoryType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 카테고리 수정 커맨드
 */
@Schema(description = "카테고리 수정 커맨드")
public record UpdateCategoryCommand(

        @Parameter(description = "상위 카테고리 식별자")
        @Schema(description = "상위 카테고리 식별자", format = "uuid")
        String parentId,

        @Parameter(description = "이름", required = true)
        @Schema(description = "이름", example = "카테고리명")
        @NotBlank
        String name,

        @Parameter(description = "타입", required = true)
        @Schema(description = "타입", example = "PUBLIC")
        @NotNull
        CategoryType type
        
) {

}
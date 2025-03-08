
package at.modoo.command;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 메뉴 수정 커맨드
 */
@Schema(description = "메뉴 수정 커맨드")
public record UpdateMenuCommand(

        @Parameter(description = "상위 메뉴 식별자")
        @Schema(description = "상위 메뉴 식별자", format = "uuid")
        String parentId,

        @Parameter(description = "이름", required = true)
        @Schema(description = "이름", example = "메뉴명")
        @NotBlank
        String name,

        @Parameter(description = "URI", required = true)
        @Schema(description = "URI", example = "/menus/list")
        @NotNull
        String uri,

        @Parameter(description = "순번", required = true)
        @Schema(description = "순번", example = "1")
        @NotNull
        Integer sequence
        
) {

}
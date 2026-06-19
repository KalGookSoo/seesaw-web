package kr.me.seesaw.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Schema(description = "메일 전송 커맨드")
@Builder
public record SendMailCommand(

        @NotBlank
        @Schema(description = "제목", example = "안녕하십니까 어디어디에 누구누구입니다.")
        String title,
        @NotBlank
        @Schema(description = "본문", example = "안녕하세요. 감사합니다.")
        String content

) {

}

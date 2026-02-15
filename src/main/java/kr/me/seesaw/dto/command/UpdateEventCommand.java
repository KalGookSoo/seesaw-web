package kr.me.seesaw.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.me.seesaw.command.UpdateArticleCommand;
import kr.me.seesaw.domain.vo.EventStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Schema(name = "UpdateEventCommand", description = "일정 수정 커맨드")
public class UpdateEventCommand {

    @Schema(description = "게시글 수정 커맨드")
    @NotNull
    private UpdateArticleCommand article;

    @Schema(description = "시작 일시")
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dtStart;

    @Schema(description = "종료 일시")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dtEnd;

    @Schema(description = "요약/제목")
    @NotBlank
    private String summary;

    @Schema(description = "상세 설명")
    private String description;

    @Schema(description = "장소")
    private String location;

    @Schema(description = "상태")
    private EventStatus status;

    @Schema(description = "시간대 식별자")
    private String tzid;

}

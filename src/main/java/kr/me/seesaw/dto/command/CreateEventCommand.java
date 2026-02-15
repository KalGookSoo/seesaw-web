package kr.me.seesaw.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.me.seesaw.command.CreateArticleCommand;
import kr.me.seesaw.domain.vo.EventStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Schema(name = "CreateEventCommand", description = "일정 생성 커맨드")
public class CreateEventCommand {

    @Schema(description = "게시글 생성 커맨드 (위지윅 본문, 제목 등 포함)")
    @NotNull
    private CreateArticleCommand article;

    @Schema(description = "시작 일시")
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dtStart;

    @Schema(description = "종료 일시")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dtEnd;

    @Schema(description = "요약/제목 (게시글 제목과 동기화 가능)")
    @NotBlank
    private String summary;

    @Schema(description = "상세 설명 (위지윅 본문과 동기화 가능)")
    private String description;

    @Schema(description = "장소")
    private String location;

    @Schema(description = "상태")
    private EventStatus status = EventStatus.CONFIRMED;

    @Schema(description = "시간대 식별자")
    private String tzid = "Asia/Seoul";

}

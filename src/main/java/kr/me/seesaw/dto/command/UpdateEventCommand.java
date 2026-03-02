package kr.me.seesaw.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.me.seesaw.domain.vo.EventStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(name = "UpdateEventCommand", description = "일정 수정 커맨드")
public class UpdateEventCommand {

    @Schema(description = "시작 일시")
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dtStart;

    @Schema(description = "종료 일시")
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dtEnd;

    @Schema(description = "제목")
    @NotBlank
    private String title;

    @Schema(description = "상세 설명")
    private String content;

    @Schema(description = "장소")
    private String location;

    @Schema(description = "상태")
    private EventStatus status;

    @Schema(description = "시간대 식별자")
    private String tzid;

    @Schema(description = "카테고리 ID")
    private String categoryId;

    @Schema(description = "첨부파일")
    private List<MultipartFile> multipartFiles = new ArrayList<>();

    @Schema(description = "이미지")
    private List<MultipartFile> inlineImages = new ArrayList<>();

}

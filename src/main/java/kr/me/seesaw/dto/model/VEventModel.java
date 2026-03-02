package kr.me.seesaw.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.VEvent;
import kr.me.seesaw.domain.vo.EventStatus;
import kr.me.seesaw.domain.vo.RecurrenceRule;
import kr.me.seesaw.model.ArticleModel;
import kr.me.seesaw.model.BaseModel;
import lombok.*;

import java.time.LocalDateTime;

@Schema(name = "VEventModel", description = "일정 모델")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class VEventModel extends BaseModel {

    @Schema(description = "게시글 식별자")
    private String articleId;

    @Schema(description = "연관 게시글 모델")
    private ArticleModel article;

    @Schema(description = "시작 일시")
    private LocalDateTime dtStart;

    @Schema(description = "종료 일시")
    private LocalDateTime dtEnd;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "상세 설명")
    private String description;

    @Schema(description = "장소")
    private String location;

    @Schema(description = "상태")
    private EventStatus status;

    @Schema(description = "반복 규칙")
    private RecurrenceRule rrule;

    @Schema(description = "시간대 식별자")
    private String tzid;

    @Schema(description = "기간")
    private String duration;

    public VEventModel(VEvent event) {
        setBaseModel(event);
        this.articleId = event.getArticleId();
        this.dtStart = event.getDtStart();
        this.dtEnd = event.getDtEnd();
        this.title = event.getSummary();
        this.location = event.getLocation();
        this.status = event.getStatus();
        this.rrule = event.getRrule();
        this.tzid = event.getTzid();
        this.duration = event.getDuration();
        if (event.getArticle() != null) {
            this.article = new ArticleModel(event.getArticle());
            this.description = this.article.getContent();
        }
    }

}

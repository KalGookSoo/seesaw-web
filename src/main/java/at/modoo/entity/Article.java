package at.modoo.entity;

import at.modoo.command.CreateArticleCommand;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.LinkedHashSet;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

/**
 * 게시글
 */
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_article")
@Comment("게시글")
@DynamicInsert
@DynamicUpdate
public class Article extends BaseEntity {

    @Comment("공개여부")
    private boolean isPublic;

    @Comment("고정여부")
    private boolean isFixed;

    @Comment("고정순서")
    private Integer fixedOrder;

    @Comment("제목")
    private String title;

    @Lob
    @Comment("본문")
    private String content;

    @Enumerated(EnumType.STRING)
    @Comment("타입")
    private ArticleType type;

    @Comment("썸네일 식별자")
    @Column(length = 36)
    private String thumbnailId;

    @Comment("카테고리 식별자")
    @Column(length = 36)
    private String categoryId;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Attachment> attachments = new LinkedHashSet<>();

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Vote> votes = new LinkedHashSet<>();

    public static Article create(CreateArticleCommand command) {
        Article article = new Article();
        article.title = command.getTitle();
        article.content = Jsoup.clean(command.getContent(), Safelist.relaxed());
        article.type = command.getArticleType();
        return article;
    }

}

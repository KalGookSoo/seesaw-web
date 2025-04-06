package at.modoo.model;

import at.modoo.command.CreateArticleCommand;
import at.modoo.command.UpdateArticleCommand;
import at.modoo.core.hierarchy.Hierarchical;
import at.modoo.model.vo.ArticleType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

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
public class Article extends AbstractHierarchical<Article> implements Hierarchical<Article, String> {

    @Comment("공개여부")
    private boolean isPublic;

    @Comment("고정여부")
    private boolean isFixed;

    @Comment("고정순서")
    private Integer fixedOrder;

    @Comment("제목")
    private String title;

    @Comment("본문")
    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Comment("타입")
    private ArticleType type;

    @Comment("썸네일 식별자")
    @Column(length = 36)
    private String thumbnailId;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
//    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    @JoinColumn(name = "thumbnail_id", referencedColumnName = "id")
    private Attachment thumbnail;

    @Comment("카테고리 식별자")
    @Column(length = 36)
    private String categoryId;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
//    @ManyToOne
//    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
//    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    @JoinTable(
//            name = "tb_article_attachment",
//            joinColumns = @JoinColumn(name = "article_id"),
//            inverseJoinColumns = @JoinColumn(name = "attachment_id")
//    )
    private Set<Attachment> attachments = new LinkedHashSet<>();

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
//    @OneToMany(mappedBy = "article", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Reply> replies = new ArrayList<>();

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
//    @OneToMany(mappedBy = "article", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<View> views = new ArrayList<>();

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
//    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    @JoinTable(
//            name = "tb_article_vote",
//            joinColumns = @JoinColumn(name = "article_id"),
//            inverseJoinColumns = @JoinColumn(name = "vote_id")
//    )
    private Set<Vote> votes = new LinkedHashSet<>();

    @Override
    public void addChild(Article child) {
        children.add(child);
        child.setParentId(getId());
        child.setParent(this);
    }

    public static Article create(CreateArticleCommand command) {
        Article article = new Article();
        article.title = command.getTitle();
        article.content = Jsoup.clean(command.getContent(), Safelist.relaxed());
        return article;
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }

    public void update(UpdateArticleCommand command) {
        this.title = command.getTitle();
        this.content = Jsoup.clean(command.getContent(), Safelist.relaxed());
    }

    public String getMaskedAuthor() {
        String createdBy = getCreatedBy();
        int visibleChars = Math.min(createdBy.length(), 4);
        return createdBy.substring(0, visibleChars) + "****";
    }

    public void addReplies(List<Reply> replies) {
        replies.stream().filter(this::isReplyForArticle).forEach(this::addReply);
    }

    public boolean isReplyForArticle(Reply reply) {
        return getId().equals(reply.getArticleId());
    }

    public void addReply(Reply reply) {
        replies.add(reply);
        reply.setArticle(this);
    }

    public void addViews(List<View> views) {
        views.stream().filter(this::isViewForArticle).forEach(this::addView);
    }

    public boolean isViewForArticle(View view) {
        return getId().equals(view.getArticleId());
    }

    public void addView(View view) {
        views.add(view);
        view.setArticle(this);
    }
}

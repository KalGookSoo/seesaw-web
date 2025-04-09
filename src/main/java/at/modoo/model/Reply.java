package at.modoo.model;

import at.modoo.core.hierarchy.Hierarchical;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_reply")
@Comment("댓글")
@DynamicInsert
public class Reply extends AbstractHierarchical<Reply> implements Hierarchical<Reply, String> {

    @Comment("공개여부")
    private boolean isPublic;

    @Comment("본문")
    @Column(columnDefinition = "TEXT")
    private String content;

    @Comment("게시글 식별자")
    @Column(length = 36)
    private String articleId;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
//    @Comment("게시글 식별자")
//    @ManyToOne
//    @JoinColumn(name = "article_id", referencedColumnName = "id")
    private Article article;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private List<Attachment> attachments = new ArrayList<>();

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private List<Vote> votes = new ArrayList<>();

    @Override
    public void addChild(Reply child) {
        children.add(child);
        child.setParentId(getId());
        child.setParent(this);
    }

    public String getMaskedAuthor() {
        String createdBy = getCreatedBy();
        int visibleChars = Math.min(createdBy.length(), 4);
        return createdBy.substring(0, visibleChars) + "****";
    }

}

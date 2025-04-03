package at.modoo.model;

import at.modoo.core.hierarchy.Hierarchical;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;

import java.util.LinkedHashSet;
import java.util.Set;

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
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    @Comment("게시글 식별자")
    @ManyToOne
    @JoinColumn(name = "article_id", referencedColumnName = "id")
    private Article article;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "tb_reply_attachment",
            joinColumns = @JoinColumn(name = "reply_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private Set<Attachment> attachments = new LinkedHashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "tb_reply_vote",
            joinColumns = @JoinColumn(name = "reply_id"),
            inverseJoinColumns = @JoinColumn(name = "vote_id")
    )
    private Set<Vote> votes = new LinkedHashSet<>();

    @Override
    public void addChild(Reply child) {
        children.add(child);
        child.setParentId(getId());
        child.setParent(this);
    }

}

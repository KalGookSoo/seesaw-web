package at.modoo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import at.modoo.core.hierarchy.Hierarchical;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;

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
@Table(name = "tb_reply")
@Comment("답글")
@DynamicInsert
public class Reply extends BaseEntity implements Hierarchical<Reply, String> {

    @Comment("공개여부")
    private boolean isPublic;

    @Comment("본문")
    @Lob
    private String content;

    @Comment("부모 식별자")
    @Column(length = 36)
    private String parentId;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private Reply parent;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private List<Reply> children = new ArrayList<>();

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

    public static Reply create(String content) {
        Reply reply = new Reply();
        reply.content = content;
        return reply;
    }

    public void update(String content) {
        this.content = content;
    }

    public void add(Reply reply) {
        children.add(reply);
        reply.parent = this;
    }

    public void remove(Reply reply) {
        children.remove(reply);
        reply.parent = null;
    }

    @Override
    public void addChild(Reply child) {
        children.add(child);
        child.parentId = getId();
        child.parent = this;
    }

}

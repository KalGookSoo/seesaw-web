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

    @Comment("게시글 식별자")
    @Column(length = 36)
    private String articleId;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Attachment> attachments = new LinkedHashSet<>();

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Vote> votes = new LinkedHashSet<>();

    @Override
    public void addChild(Reply child) {
        children.add(child);
        child.parentId = getId();
        child.parent = this;
    }

}

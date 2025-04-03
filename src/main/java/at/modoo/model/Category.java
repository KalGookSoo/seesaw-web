package at.modoo.model;

import at.modoo.core.hierarchy.Hierarchical;
import at.modoo.model.vo.CategoryType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

/**
 * 카테고리
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_category")
@DynamicInsert
@DynamicUpdate
@Comment("카테고리")
public class Category extends AbstractHierarchical<Category> implements Hierarchical<Category, String> {

    @Comment("이름")
    private String name;

    @Comment("설명")
    private String description;

    @Enumerated(EnumType.STRING)
    @Comment("타입")
    private CategoryType type;

    @Comment("공개여부")
    private boolean isPublic;

    @Comment("순서")
    private Integer sequence;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    @Comment("사이트 식별자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    private Site site;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(mappedBy = "category", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Article> articles = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "category")
    @JsonManagedReference
    private List<Notification> notifications = new ArrayList<>();

    @Override
    public void addChild(Category child) {
        children.add(child);
        child.setParentId(getId());
        child.setParent(this);
    }

}

package at.modoo.model;

import at.modoo.core.hierarchy.Hierarchical;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_menu")
@DynamicInsert
@DynamicUpdate
@Comment("메뉴")
public class Menu extends AbstractHierarchical<Menu> implements Hierarchical<Menu, String> {

    @Comment("이름")
    private String name;

    @Comment("URI")
    private String uri;

    @Comment("순번")
    private Integer sequence;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "tb_menu_authority",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private Set<Authority> authorities = new LinkedHashSet<>();

    @Override
    public void addChild(Menu child) {
        children.add(child);
        child.setParentId(getId());
        child.setParent(this);
    }

}

package at.modoo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import at.modoo.command.CreateMenuCommand;
import at.modoo.command.UpdateMenuCommand;
import at.modoo.core.hierarchy.Hierarchical;
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
public class Menu extends BaseEntity implements Hierarchical<Menu> {

    @Comment("이름")
    private String name;

    @Comment("URI")
    private String uri;

    @Comment("순번")
    private Integer sequence;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Comment("부모 식별자")
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @JsonBackReference
    private Menu parent;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "parent")
    @JsonManagedReference
    private List<Menu> children = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "tb_menu_authority",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private final Set<Authority> authorities = new LinkedHashSet<>();

    public static Menu create(CreateMenuCommand command, Menu parent) {
        Menu menu = new Menu();
        menu.parent = parent;
        menu.name = command.name();
        menu.uri = command.uri();
        menu.sequence = command.sequence();
        return menu;
    }

    public void update(UpdateMenuCommand command, Menu parent) {
        this.parent = parent;
        this.name = command.name();
        this.uri = command.uri();
        this.sequence = command.sequence();
    }

    @Override
    public void addChild(Menu child) {
        children.add(child);
        parent = this;
    }

}

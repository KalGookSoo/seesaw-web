package at.modoo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.LinkedHashSet;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_role")
@Comment("역할")
@DynamicInsert
@DynamicUpdate
public class Role extends BaseEntity {

    @Comment("참조 식별자")
    @Column(length = 36)
    private String referenceId;

    @Comment("이름")
    private String name;

    @Comment("별칭")
    private String alias;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
//    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new LinkedHashSet<>();

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
//    @ManyToMany(mappedBy = "roles")
    private Set<Menu> menus = new LinkedHashSet<>();

    public static Role create(String referenceId, String name, String alias) {
        Role role = new Role();
        role.referenceId = referenceId;
        role.name = name;
        role.alias = alias;
        return role;
    }
}

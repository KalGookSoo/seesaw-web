package at.modoo.model;

import at.modoo.model.vo.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.LinkedHashSet;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

/**
 * 권한
 */
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_authority")
@Comment("권한")
@DynamicInsert
@DynamicUpdate
public class Authority extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Comment("이름")
    private Role name;

    @Comment("별칭")
    private String alias;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    @ManyToMany(mappedBy = "authorities")
    private Set<User> users = new LinkedHashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    @ManyToMany(mappedBy = "authorities")
    private Set<Menu> menus = new LinkedHashSet<>();

    private Authority(Role role, User user) {
        this.name = role;
        this.users.add(user);
    }

    public static Authority create(Role role, User user) {
        return new Authority(role, user);
    }

}

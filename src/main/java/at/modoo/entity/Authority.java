package at.modoo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import at.modoo.command.AuthoritySaveCommand;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
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

    @Comment("이름")
    private String name;

    @Comment("별칭")
    private String alias;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    @ManyToMany(mappedBy = "authorities")
    private final Set<User> users = new LinkedHashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    @ManyToMany(mappedBy = "authorities")
    private final Set<Menu> menus = new LinkedHashSet<>();

    private Authority(String name, User user) {
        this.name = name;
        this.users.add(user);
    }

    /**
     * 권한 정적 팩토리 메서드
     * @param name 이름
     * @param user 계정
     * @return 권한
     */
    public static Authority create(String name, User user) {
        if (!name.startsWith("ROLE_")) {
            throw new IllegalArgumentException("name must start with 'ROLE_'\n name: " + name);
        }
        return new Authority(name, user);
    }

    public static Authority create(AuthoritySaveCommand command) {
        if (!command.getName().startsWith("ROLE_")) {
            throw new IllegalArgumentException("name must start with 'ROLE_'\n name: " + command.getName());
        }
        Authority authority = new Authority();
        authority.name = command.getName();
        authority.alias = command.getAlias();
        return authority;
    }

    public void update(AuthoritySaveCommand command) {
        this.name = command.getName();
        this.alias = command.getAlias();
    }

}

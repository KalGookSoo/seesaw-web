package kr.me.seesaw.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_menu_role")
@Comment("메뉴 역할 매핑")
@DynamicInsert
@DynamicUpdate
public class MenuRole extends BaseEntity {

    private String menuId;

    private String roleId;

    public static MenuRole create(String menuId, String roleId) {
        MenuRole menuRole = new MenuRole();
        menuRole.menuId = menuId;
        menuRole.roleId = roleId;
        return menuRole;
    }

}

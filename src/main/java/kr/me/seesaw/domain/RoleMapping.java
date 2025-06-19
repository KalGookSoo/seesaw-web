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
@Table(name = "tb_role_mapping")
@Comment("역할 매핑")
@DynamicInsert
@DynamicUpdate
public class RoleMapping extends BaseEntity {

    private String roleId;

    private String userId;

    private String siteId;

    public static RoleMapping create(String roleId, String userId, String siteId) {
        RoleMapping roleMapping = new RoleMapping();
        roleMapping.roleId = roleId;
        roleMapping.userId = userId;
        roleMapping.siteId = siteId;
        return roleMapping;
    }

}

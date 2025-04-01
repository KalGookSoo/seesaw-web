package at.modoo.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ROLE_ADMIN("최고관리자"),
    ROLE_MANAGER("관리자"),
    ROLE_USER("일반사용자");
    private final String description;
}

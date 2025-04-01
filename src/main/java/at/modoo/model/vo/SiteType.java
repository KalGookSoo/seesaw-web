package at.modoo.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 사이트 타입
 */
@Getter
@AllArgsConstructor
public enum SiteType {
    INFORMATION("정보"),
    IMAGE("이미지"),
    POSTER("포스터"),
    BUTTON("버튼"),
    FREEDOM("자유");
    private final String description;
}

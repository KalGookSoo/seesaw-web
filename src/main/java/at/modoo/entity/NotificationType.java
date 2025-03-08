package at.modoo.entity;

import lombok.Getter;

/**
 * 알림 타입
 */
@Getter
public enum NotificationType {
    TOKTOK("톡톡"),
    SMS("문자"),
    EMAIL("이메일");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }
}

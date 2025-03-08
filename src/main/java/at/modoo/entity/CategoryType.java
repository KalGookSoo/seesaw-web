package at.modoo.entity;

import lombok.Getter;

/**
 * 카테고리 타입
 */
@Getter
public enum CategoryType {
    LIST("목록"),
    CARD("카드");

    private final String description;

    CategoryType(String description) {
        this.description = description;
    }
}

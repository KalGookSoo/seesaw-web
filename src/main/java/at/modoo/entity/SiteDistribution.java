package at.modoo.entity;

import lombok.Getter;

/**
 * 사이트 분류
 */
@Getter
public enum SiteDistribution {
    INDIVIDUAL("개인"),
    COMPANY("업체"),
    ORGANIZATION("기관"),
    GROUP("단체"),
    ETC("기타");

    private final String description;

    SiteDistribution(String description) {
        this.description = description;
    }
}

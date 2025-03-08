package at.modoo.entity;

import lombok.Getter;

/**
 * 투표 타입
 */
@Getter
public enum VoteType {

    APPROVE("찬성"),
    DISAPPROVE("반대");

    private final String description;

    VoteType(String description) {
        this.description = description;
    }

}

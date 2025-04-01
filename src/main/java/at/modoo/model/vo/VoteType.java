package at.modoo.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 투표 타입
 */
@Getter
@AllArgsConstructor
public enum VoteType {
    APPROVE("찬성"),
    DISAPPROVE("반대");
    private final String description;
}

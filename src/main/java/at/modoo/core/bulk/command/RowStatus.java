package at.modoo.core.bulk.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RowStatus {
    C("생성"),
    R("읽기"),
    U("수정"),
    D("삭제");
    private final String description;
}

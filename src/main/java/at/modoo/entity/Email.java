package at.modoo.entity;


import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Optional;

import static lombok.AccessLevel.PROTECTED;

/**
 * 이메일
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public class Email implements Serializable {

    private String id;

    private String domain;

    /**
     * 이메일 생성자
     * @param id     이메일 아이디
     * @param domain 이메일 도메인
     */
    public Email(String id, String domain) {
        this.id = id;
        this.domain = domain;
    }

    @Override
    public String toString() {
        return Optional.ofNullable(id).orElse("") + "@" + Optional.ofNullable(domain).orElse("");
    }

}

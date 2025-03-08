package at.modoo.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 계정 수정 커맨드
 * @param name                이름
 * @param emailId             이메일 아이디
 * @param emailDomain         이메일 도메인
 * @param firstContactNumber  첫 번째 연락처 번호
 * @param middleContactNumber 두 번째 연락처 번호
 * @param lastContactNumber   마지막 연락처 번호
 */
public record UpdateUserCommand(
        @NotBlank
        @Size(min = 2, max = 20)
        String name,

        String emailId,

        String emailDomain,

        String firstContactNumber,

        String middleContactNumber,

        String lastContactNumber
) {}
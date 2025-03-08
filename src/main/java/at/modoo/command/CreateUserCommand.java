package at.modoo.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 계정 생성 커맨드
 * @param username            계정명
 * @param password            패스워드
 * @param name                이름
 * @param emailId             이메일 아이디
 * @param emailDomain         이메일 도메인
 * @param firstContactNumber  연락처 첫번째 자리
 * @param middleContactNumber 연락처 중간 자리
 * @param lastContactNumber   연락처 마지막 자리
 */
public record CreateUserCommand(
        @NotBlank
        @Size(min = 3, max = 20)
        String username,

        @NotBlank
        @Size(min = 8)
        String password,

        @NotBlank
        @Size(min = 2, max = 20)
        String name,

        String emailId,

        String emailDomain,

        String firstContactNumber,

        String middleContactNumber,

        String lastContactNumber
) {
}
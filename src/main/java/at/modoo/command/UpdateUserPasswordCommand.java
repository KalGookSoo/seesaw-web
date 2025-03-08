package at.modoo.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 계정 패스워드 수정 커맨드
 * @param originPassword 기존 패스워드
 * @param newPassword    수정할 패스워드
 */
public record UpdateUserPasswordCommand(
        @NotNull
        @NotBlank
        String originPassword,

        @NotNull
        @NotBlank
        String newPassword
) {}
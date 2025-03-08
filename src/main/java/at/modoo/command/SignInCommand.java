 package at.modoo.command;

 import jakarta.validation.constraints.NotBlank;
 import jakarta.validation.constraints.NotNull;

 /**
  * 계정 인증 커맨드
  * @param username 계정명
  * @param password 패스워드
  */
 public record SignInCommand(

         @NotBlank
         @NotNull
         String username,

         @NotBlank
         @NotNull
         String password

 ) {}
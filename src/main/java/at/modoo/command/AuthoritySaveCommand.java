package at.modoo.command;

import at.modoo.core.bulk.command.Command;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Schema(description = "권한 저장 커맨드")
@Data
public class AuthoritySaveCommand extends Command<String> {

    private String id;

    @NotNull
    @Pattern(regexp = "^ROLE_.*", message = "The name must start with 'ROLE_'")
    private String name;

    private String alias;

}

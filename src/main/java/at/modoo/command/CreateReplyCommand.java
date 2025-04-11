package at.modoo.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CreateReplyCommand implements Serializable {

    private boolean isPublic;

    @NotNull
    @NotBlank
    private String articleId;

    @NotNull
    @NotBlank
    private String content;

    private List<MultipartFile> multipartFiles = new ArrayList<>();

}

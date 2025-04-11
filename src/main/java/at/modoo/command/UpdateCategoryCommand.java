package at.modoo.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateCategoryCommand implements Serializable {
    private String id;
    private String content;
}

package at.modoo.service;

import at.modoo.command.CreateCategoryCommand;
import at.modoo.command.UpdateCategoryCommand;
import at.modoo.model.Category;

public interface CategoryService {
    Category create(CreateCategoryCommand command);
    Category find(String id);
    Category update(String id, UpdateCategoryCommand command);
    void delete(String id);
}

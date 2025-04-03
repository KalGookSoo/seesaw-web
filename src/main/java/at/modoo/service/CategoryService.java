package at.modoo.service;

import at.modoo.command.CreateCategoryCommand;
import at.modoo.command.UpdateCategoryCommand;
import at.modoo.model.Category;

import java.util.List;

public interface CategoryService {
    Category create(CreateCategoryCommand command);
    List<Category> findAll();
    Category find(String id);
    Category update(String id, UpdateCategoryCommand command);
    void delete(String id);
}

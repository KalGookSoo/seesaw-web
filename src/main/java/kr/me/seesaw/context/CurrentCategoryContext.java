package kr.me.seesaw.context;

import kr.me.seesaw.domain.Category;
import kr.me.seesaw.model.CategoryModel;
import kr.me.seesaw.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.NoSuchElementException;

@RequestScope
@RequiredArgsConstructor
@Service
public class CurrentCategoryContext implements CategoryContext {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryModel getCategory(String id) {
        logger.debug("카테고리 조회: id={}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 카테고리가 존재하지 않습니다. id: " + id));
        return new CategoryModel(category);
    }

}

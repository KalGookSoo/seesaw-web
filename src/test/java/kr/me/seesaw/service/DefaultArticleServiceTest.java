package kr.me.seesaw.service;

import jakarta.persistence.EntityManager;
import kr.me.seesaw.command.CreateArticleCommand;
import kr.me.seesaw.core.authentication.AnonymousPrincipalProvider;
import kr.me.seesaw.domain.Article;
import kr.me.seesaw.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;


@ActiveProfiles("test")
@DataJpaTest
class DefaultArticleServiceTest {

    @TempDir
    private Path tempDir;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private ViewRepository viewRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    private ArticleService articleService;

    @BeforeEach
    void setUp() {
        String filepath = tempDir.toString();
        articleService = new DefaultArticleService(
                filepath,
                articleRepository,
                new ArticleSearchRepository(entityManager),
                replyRepository,
                viewRepository,
                attachmentRepository,
                new AnonymousPrincipalProvider()
        );
    }

    @Test
    @DisplayName("게시글 생성")
    void createCase1Test() throws IOException {
        // Given
        CreateArticleCommand command = new CreateArticleCommand();
        command.setCategoryId(UUID.randomUUID().toString());
        command.setTitle("title");
        command.setContent("<img src=\"blob:" + UUID.randomUUID() + "\">");

        command.setInlineImages(List.of(new MockMultipartFile("image", "image.png", "image/png", "image".getBytes())));
        command.setMultipartFiles(List.of(new MockMultipartFile("text", "text.txt", "text/plain", "text".getBytes())));

        // When
        Article article = articleService.create(command);
        entityManager.flush();

        // Then
        Assertions.assertNotNull(article);
        Assertions.assertEquals(1, article.getVersion());// 인라인이미지 링크 치환때문에 insert 후에 update가 발생함
    }

}

package at.modoo.service;

import at.modoo.command.CreateArticleCommand;
import at.modoo.command.UpdateArticleCommand;
import at.modoo.core.file.FileIOService;
import at.modoo.model.Article;
import at.modoo.model.Attachment;
import at.modoo.model.Reply;
import at.modoo.model.View;
import at.modoo.repository.*;
import at.modoo.search.ArticleSearch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Transactional
@Service
public class DefaultArticleService implements ArticleService {

    @Value("${at.modoo.filepath}")
    private final String filepath;

    private final ArticleRepository articleRepository;

    private final ArticleSearchRepository articleSearchRepository;

    private final ReplyRepository replyRepository;

    private final ViewRepository viewRepository;

    private final AttachmentRepository attachmentRepository;

    public DefaultArticleService(
            @Value("${at.modoo.filepath}") String filepath,
            ArticleRepository articleRepository,
            ArticleSearchRepository articleSearchRepository,
            ReplyRepository replyRepository,
            ViewRepository viewRepository,
            AttachmentRepository attachmentRepository
    ) {
        this.filepath = filepath;
        this.articleRepository = articleRepository;
        this.articleSearchRepository = articleSearchRepository;
        this.replyRepository = replyRepository;
        this.viewRepository = viewRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Article> findAll(Pageable pageable, ArticleSearch search) {
        Page<Article> page = articleSearchRepository.search(pageable, search);

        List<String> articleIds = page.getContent().stream().map(Article::getId).toList();

        List<Reply> replies = replyRepository.findAllByArticleIdIn(articleIds);
        page.getContent().forEach(article -> article.addReplies(replies));

        List<View> views = viewRepository.findAllByArticleIdIn(articleIds);
        page.getContent().forEach(article -> article.addViews(views));

        // attachments
        return page;
    }

    @Override
    public Page<Article> view(String categoryId, Pageable pageable) {
        // 조회수 증가
        return articleRepository.findAllByCategoryId(categoryId, pageable);
    }

    @Override
    public Page<Article> findAllByCategoryId(String categoryId, Pageable pageable) {
        return articleRepository.findAllByCategoryId(categoryId, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Article find(String id) {
        return articleRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Article create(CreateArticleCommand command) throws IOException {
        Article article = Article.create(command);
        for (MultipartFile multipartFile : command.getMultipartFiles()) {
            Attachment attachment = Attachment.create(filepath, multipartFile);
            article.addAttachment(attachment);
            writeFile(attachment.getAbsolutePath(), multipartFile.getBytes());
        }
        articleRepository.save(article);
        attachmentRepository.saveAll(article.getAttachments());
        return article;
    }

    @Override
    public Article update(String id, UpdateArticleCommand command) throws IOException {
        Article article = articleRepository.getReferenceById(id);
        article.update(command);
        for (MultipartFile multipartFile : command.getMultipartFiles()) {
            Attachment attachment = Attachment.create(filepath, multipartFile);
            article.addAttachment(attachment);
            writeFile(attachment.getAbsolutePath(), multipartFile.getBytes());
        }
        articleRepository.save(article);
        attachmentRepository.saveAll(article.getAttachments());
        return article;
    }

    @Override
    public void delete(String id) {
        Article article = articleRepository.getReferenceById(id);
        articleRepository.delete(article);
    }

    @Override
    public boolean isOwner(String id, String username) {
        Article article = find(id);
        return article.getCreatedBy().equals(username);
    }

    private static void writeFile(String pathname, byte[] bytes) {
        try {
            FileIOService.write(pathname, bytes);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

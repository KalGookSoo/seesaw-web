package at.modoo.service;

import at.modoo.command.CreateArticleCommand;
import at.modoo.command.UpdateArticleCommand;
import at.modoo.core.authentication.PrincipalProvider;
import at.modoo.core.file.FileIOService;
import at.modoo.model.*;
import at.modoo.repository.*;
import at.modoo.search.ArticleSearch;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

    private final PrincipalProvider principalProvider;

    private final EntityManager entityManager;

    public DefaultArticleService(
            @Value("${at.modoo.filepath}") String filepath,
            ArticleRepository articleRepository,
            ArticleSearchRepository articleSearchRepository,
            ReplyRepository replyRepository,
            ViewRepository viewRepository,
            AttachmentRepository attachmentRepository,
            PrincipalProvider principalProvider,
            EntityManager entityManager
    ) {
        this.filepath = filepath;
        this.articleRepository = articleRepository;
        this.articleSearchRepository = articleSearchRepository;
        this.replyRepository = replyRepository;
        this.viewRepository = viewRepository;
        this.attachmentRepository = attachmentRepository;
        this.principalProvider = principalProvider;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Article> findAll(Pageable pageable, ArticleSearch search) {
        Page<Article> page = articleSearchRepository.search(pageable, search);

        List<String> articleIds = page.getContent().stream().map(Article::getId).toList();

        List<Reply> replies = replyRepository.findAllByArticleIdIn(articleIds);
        replies.sort(Comparator.comparing(BaseEntity::getCreatedDate));
        page.getContent().forEach(article -> article.joinReplies(replies));

        List<View> views = viewRepository.findAllByArticleIdIn(articleIds);
        views.sort(Comparator.comparing(BaseEntity::getCreatedDate));
        page.getContent().forEach(article -> article.joinViews(views));

        List<Attachment> attachments = attachmentRepository.findAllByReferenceIdIn(articleIds)
                .stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate))
                .toList();
        page.getContent().forEach(article -> article.joinAttachments(attachments));
        return page;
    }

    @Override
    public Page<Article> findAllByCategoryId(String categoryId, Pageable pageable) {
        Page<Article> page = articleRepository.findAllByCategoryId(categoryId, pageable);

        List<String> articleIds = page.getContent().stream().map(Article::getId).toList();

        List<Reply> replies = replyRepository.findAllByArticleIdIn(articleIds);
        replies.sort(Comparator.comparing(BaseEntity::getCreatedDate));
        page.getContent().forEach(article -> article.joinReplies(replies));

        List<View> views = viewRepository.findAllByArticleIdIn(articleIds);
        views.sort(Comparator.comparing(BaseEntity::getCreatedDate));
        page.getContent().forEach(article -> article.joinViews(views));

        List<Attachment> attachments = attachmentRepository.findAllByReferenceIdIn(articleIds)
                .stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate))
                .toList();
        page.getContent().forEach(article -> article.joinAttachments(attachments));
        return page;
    }

    @Transactional(readOnly = true)
    @Override
    public Article find(String id) {
        return articleRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Article create(CreateArticleCommand command) throws IOException {

        // 생성될 게시글의 식별자를 참조하기위해 먼저 게시글을 저장한다.
        Article article = Article.create(command);
        articleRepository.save(article);

        Document document = Jsoup.parse(command.getContent());
        Iterator<Element> iterator = document.select("img[src*=\"blob:\"]").iterator();

        for (MultipartFile multipartFile : command.getInlineImages()) {
            Attachment attachment = Attachment.create(article.getId(), Attachment.Type.INLINE_IMAGE, multipartFile);
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), multipartFile.getBytes());
            attachmentRepository.save(attachment);

            // request에서 host 주소 추출
            // TODO 이거 인터페이스 분리하여 외부에서 의존관계 주입받아야 함
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String host = request.getHeader("host");
            String scheme = request.getScheme();
            String url = scheme + "://" + host + "/api/attachments/" + attachment.getId();

            // images의 src를 첨부파일을 생성 후 "/api/attachments/{id}"로 치환한다.
            if (iterator.hasNext()) {
                Element element = iterator.next();
                element.attr("src", url);
            }
        }

        // 첨부파일
        for (MultipartFile multipartFile : command.getMultipartFiles()) {
            Attachment attachment = Attachment.create(article.getId(), Attachment.Type.ATTACHMENT, multipartFile);
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), multipartFile.getBytes());
            attachmentRepository.save(attachment);
        }

        // 인라인이미지 링크를 첨부파일 API로 치환한 본문으로 재할당한다
        article.setContent(document.body().html());
        articleRepository.save(article);
        return article;
    }

    @Override
    public Article update(String id, UpdateArticleCommand command) throws IOException {
        Article article = articleRepository.getReferenceById(id);

        Document existingContent = Jsoup.parse(article.getContent());
        Elements existingImages = existingContent.select("img[src*=\"/api/attachments/\"]");
        
        List<String> deletedAttachmentIds = new ArrayList<>();
        
        Document newContent = Jsoup.parse(command.getContent());
        Elements newImages = newContent.select("img[src*=\"/api/attachments/\"]");

        // src는 "/"로 스플릿하여 마지막 요소를 uuid4 패턴의 문자열이다.
        for (Element existingImage : existingImages) {
            String existingSrc = existingImage.attr("src");
            boolean isPresentInNewImages = newImages.stream()
                    .anyMatch(newImage -> newImage.attr("src").equals(existingSrc));
            if (!isPresentInNewImages) {
                deletedAttachmentIds.add(existingSrc.substring(existingSrc.lastIndexOf("/") + 1));
            }
        }

        // 수정하면서 삭제한 이미지를 삭제
        List<Attachment> attachments = attachmentRepository.findAllById(deletedAttachmentIds);
        attachmentRepository.deleteAllInBatch(attachments);
        attachments.stream().map(attachment -> filepath + attachment.getPathName() + File.separator + attachment.getName()).forEach(FileIOService::delete);

        Iterator<Element> iterator = newContent.select("img[src*=\"blob:\"]").iterator();

        for (MultipartFile multipartFile : command.getInlineImages()) {
            Attachment attachment = Attachment.create(article.getId(), Attachment.Type.INLINE_IMAGE, multipartFile);
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), multipartFile.getBytes());
            attachmentRepository.save(attachment);

            // request에서 host 주소 추출
            // TODO 이거 인터페이스 분리하여 외부에서 의존관계 주입받아야 함
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String host = request.getHeader("host");
            String scheme = request.getScheme();
            String url = scheme + "://" + host + "/api/attachments/" + attachment.getId();

            // images의 src를 첨부파일을 생성 후 "/api/attachments/{id}"로 치환한다.
            if (iterator.hasNext()) {
                Element element = iterator.next();
                element.attr("src", url);
            }
        }

        command.setContent(newContent.body().html());
        article.update(command);
        articleRepository.save(article);

        // TODO 인라인이미지 생성 후 바인딩

        // 첨부파일
        for (MultipartFile multipartFile : command.getMultipartFiles()) {
            Attachment attachment = Attachment.create(article.getId(), Attachment.Type.ATTACHMENT, multipartFile);
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), multipartFile.getBytes());
            attachmentRepository.save(attachment);
        }
        return article;
    }

    @Override
    public void delete(String id) {
        Article article = articleRepository.getReferenceById(id);
        List<Reply> replies = replyRepository.findAllByArticleIdIn(Collections.singletonList(article.getId()));
        replyRepository.deleteAllInBatch(replies);
        List<View> views = viewRepository.findAllByArticleIdIn(Collections.singletonList(article.getId()));
        viewRepository.deleteAllInBatch(views);
        List<Attachment> attachments = attachmentRepository.findAllByReferenceIdIn(Collections.singletonList(article.getId()));
        attachmentRepository.deleteAllInBatch(attachments);
        attachments.stream().map(attachment -> filepath + attachment.getPathName() + File.separator + attachment.getName()).forEach(FileIOService::delete);

        articleRepository.delete(article);
    }

    @Override
    public boolean isOwner(String id, String username) {
        Article article = find(id);
        return article.getCreatedBy().equals(username);
    }

    @Override
    public List<Article> getFixedArticles(String categoryId, boolean fixed, Sort sort) {
        return articleRepository.findAllByCategoryIdAndFixed(categoryId, fixed, sort);
    }

    private void writeFile(String pathname, byte[] bytes) {
        try {
            FileIOService.write(pathname, bytes);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void increaseView(String articleId) {
        View view = View.create(articleId);
        Object principal = principalProvider.getAuthentication().getPrincipal();
        // 동일인물 중복 조회수 불허
        List<View> views = viewRepository.findAllByArticleIdIn(Collections.singletonList(articleId));

    }

}

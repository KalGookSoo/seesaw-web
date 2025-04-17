package at.modoo.service;

import at.modoo.command.CreateArticleCommand;
import at.modoo.command.UpdateArticleCommand;
import at.modoo.core.authentication.PrincipalProvider;
import at.modoo.core.file.FileIOService;
import at.modoo.core.jsoup.PatternMatcher;
import at.modoo.model.*;
import at.modoo.repository.*;
import at.modoo.search.ArticleSearch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public DefaultArticleService(
            @Value("${at.modoo.filepath}") String filepath,
            ArticleRepository articleRepository,
            ArticleSearchRepository articleSearchRepository,
            ReplyRepository replyRepository,
            ViewRepository viewRepository,
            AttachmentRepository attachmentRepository,
            PrincipalProvider principalProvider
    ) {
        this.filepath = filepath;
        this.articleRepository = articleRepository;
        this.articleSearchRepository = articleSearchRepository;
        this.replyRepository = replyRepository;
        this.viewRepository = viewRepository;
        this.attachmentRepository = attachmentRepository;
        this.principalProvider = principalProvider;
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
    public Page<Article> view(String categoryId, Pageable pageable) {
        // 조회수 증가
        Page<Article> page = findAllByCategoryId(categoryId, pageable);
        if (page.hasContent()) {
//            increaseView(page.getContent().get(0).getId());
        }
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
// TODO 저장할 때 content에 img src 에서 /api/attachments인것들 추출하여 referenceId 삽입하여 update할 것 여집합은 삭제할 것
    @Override
    public Article create(CreateArticleCommand command) throws IOException {
        Article article = Article.create(command);
        articleRepository.save(article);

        // 미리 생성된 첨부파일 바인딩
        Document document = Jsoup.parse(article.getContent());
        Elements images = document.select("img[src]");
        String inlineImagePattern = "/api/attachments/(" + PatternMatcher.UUID + ")$";
        Pattern pattern = Pattern.compile(inlineImagePattern);
        List<String> attachmentIds = new ArrayList<>();
        for (Element image : images) {
            String src = image.attr("src");
            Matcher matcher = pattern.matcher(src);

            /*
             * ! find method는 사이드이펙트가 있을 수 있음. 디버그 시 평가하면 결과가 달라짐
             */
            if (matcher.find()) {
                String uuidStr = matcher.group(1);
                attachmentIds.add(uuidStr);
            }
        }
        List<Attachment> inlineImages = attachmentRepository.findAllById(attachmentIds).stream().filter(attachment -> Objects.isNull(attachment.getReferenceId())).toList();
        inlineImages.forEach(image -> image.lazySetReferenceId(article.getId()));
        article.joinAttachments(inlineImages);

        // 첨부파일
        for (MultipartFile multipartFile : command.getMultipartFiles()) {
            Attachment attachment = Attachment.create(article.getId(), Attachment.Type.ATTACHMENT, multipartFile);
            article.addAttachment(attachment);
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), multipartFile.getBytes());
        }
        attachmentRepository.saveAll(article.getAttachments());
        return article;
    }

    @Override
    public Article update(String id, UpdateArticleCommand command) throws IOException {
        Article article = articleRepository.getReferenceById(id);
        article.update(command);
        articleRepository.save(article);

        // 미리 생성된 첨부파일 바인딩
        Document document = Jsoup.parse(article.getContent());
        Elements images = document.select("img[src]");
        String inlineImagePattern = "/api/attachments/(" + PatternMatcher.UUID + ")$";
        Pattern pattern = Pattern.compile(inlineImagePattern);
        List<String> attachmentIds = new ArrayList<>();
        for (Element image : images) {
            String src = image.attr("src");
            Matcher matcher = pattern.matcher(src);

            /*
             * ! find method는 사이드이펙트가 있을 수 있음. 디버그 시 평가하면 결과가 달라짐
             */
            if (matcher.find()) {
                String uuidStr = matcher.group(1);
                attachmentIds.add(uuidStr);
            }
        }
        List<Attachment> inlineImages = attachmentRepository.findAllById(attachmentIds).stream().filter(attachment -> Objects.isNull(attachment.getReferenceId())).toList();
        inlineImages.forEach(image -> image.lazySetReferenceId(article.getId()));
        article.joinAttachments(inlineImages);

        // 첨부파일
        for (MultipartFile multipartFile : command.getMultipartFiles()) {
            Attachment attachment = Attachment.create(article.getId(), Attachment.Type.ATTACHMENT, multipartFile);
            article.addAttachment(attachment);
            writeFile(filepath + attachment.getPathName() + File.separator + attachment.getName(), multipartFile.getBytes());
        }
        attachmentRepository.saveAll(article.getAttachments());
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
        articleRepository.delete(article);
    }

    @Override
    public boolean isOwner(String id, String username) {
        Article article = find(id);
        return article.getCreatedBy().equals(username);
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

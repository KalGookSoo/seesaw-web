package kr.me.seesaw.service;

import kr.me.seesaw.domain.Article;
import kr.me.seesaw.domain.Category;
import kr.me.seesaw.domain.RoleMapping;
import kr.me.seesaw.domain.User;
import kr.me.seesaw.domain.vo.Email;
import kr.me.seesaw.domain.vo.RoleName;
import kr.me.seesaw.repository.ArticleRepository;
import kr.me.seesaw.repository.CategoryRepository;
import kr.me.seesaw.repository.RoleMappingRepository;
import kr.me.seesaw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminNotificationService implements NotificationService {

    private final RoleMappingRepository roleMappingRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final ArticleRepository articleRepository;

    private final MailProperties properties;

    private final MailService mailService;

    @Override
    public void sendOnArticleCreated(String categoryId, String articleId, String title, String content) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("카테고리를 찾을 수 없습니다. categoryId: " + categoryId));

        Predicate<RoleName> isAdmin = role -> RoleName.ROLE_ADMIN == role;
        Predicate<RoleName> isManager = role -> RoleName.ROLE_MANAGER == role;
        Set<String> roleNames = Arrays.stream(RoleName.values()).filter(isAdmin.or(isManager))
                .map(RoleName::name)
                .collect(Collectors.toSet());

        log.debug("사이트에 종속된 ADMIN or MANAGER 목록을 조회합니다.");
        Collection<RoleMapping> mappings = roleMappingRepository.findAllBySiteIdAndRoleNameIn(category.getSiteId(), roleNames);
        Set<String> userIds = mappings.stream()
                .map(RoleMapping::getUserId)
                .collect(Collectors.toSet());

        String articleUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/articles/{id}")
                .queryParam("categoryId", categoryId)
                .queryParam("categoryType", category.getType())
                .buildAndExpand(Map.of("id", articleId))
                .toUriString();

        Map<String, String> values = Map.of(
                "title", title,
                "content", content.length() > 20 ? content.substring(0, 20) + "..." : content,
                "articleUrl", articleUrl
        );

        String[] emails = userRepository.findAllByIdIn(userIds).stream()
                .map(User::getEmail)
                .filter(Predicate.not(Email::isEmpty))
                .map(Email::toString)
                .toArray(String[]::new);

        mailService.send(properties.getUsername(), emails, "[알림] 게시글이 생성되었습니다.", "mail/article_created", values);
        logPreviewUrl("/mail/preview/article-created", values);
    }

    @Override
    public void sendOnReplyCreated(String articleId, String replyId, String content) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다. articleId: " + articleId));
        String categoryId = article.getCategoryId();
        Category category = article.getCategory();

        Predicate<RoleName> isAdmin = role -> RoleName.ROLE_ADMIN == role;
        Predicate<RoleName> isManager = role -> RoleName.ROLE_MANAGER == role;
        Set<String> roleNames = Arrays.stream(RoleName.values()).filter(isAdmin.or(isManager))
                .map(RoleName::name)
                .collect(Collectors.toSet());

        log.debug("사이트에 종속된 ADMIN or MANAGER 목록을 조회합니다.");
        Collection<RoleMapping> mappings = roleMappingRepository.findAllBySiteIdAndRoleNameIn(category.getSiteId(), roleNames);
        Set<String> userIds = mappings.stream()
                .map(RoleMapping::getUserId)
                .collect(Collectors.toSet());

        String articleUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/articles/{id}")
                .queryParam("categoryId", categoryId)
                .queryParam("categoryType", category.getType())
                .fragment(replyId)
                .buildAndExpand(Map.of("id", articleId))
                .toUriString();

        Map<String, String> values = Map.of(
                "title", article.getTitle(),
                "content", content.length() > 20 ? content.substring(0, 20) + "..." : content,
                "articleUrl", articleUrl,
                "replyId", replyId
        );

        String[] emails = userRepository.findAllByIdIn(userIds).stream()
                .map(User::getEmail)
                .filter(Predicate.not(Email::isEmpty))
                .map(Email::toString)
                .toArray(String[]::new);

        mailService.send(properties.getUsername(), emails, "[알림] 게시글이 생성되었습니다.", "mail/reply_created", values);
        logPreviewUrl("/mail/preview/reply-created", values);
    }

    public void logPreviewUrl(String path, Map<String, String> values) {
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(path)
                .queryParam("title", values.get("title"))
                .queryParam("content", values.get("content"))
                .queryParam("articleUrl", values.get("articleUrl"));

        if (values.containsKey("replyId")) {
            builder.fragment(values.get("replyId"));
        }

        String previewUrl = builder
                .encode()
                .build()
                .toUriString();
        log.info("미리보기 URL: {}", previewUrl);
    }

}

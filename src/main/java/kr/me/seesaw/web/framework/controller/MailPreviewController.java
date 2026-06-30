package kr.me.seesaw.web.framework.controller;

import kr.me.seesaw.web.domain.service.*;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mail/preview")
public class MailPreviewController {

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/send-to-report")
    public String sendToReport(
            @RequestParam(required = false, defaultValue = "example@email.com") String from,
            @RequestParam(required = false, defaultValue = "대전포스트잇 ♣대전독서모임") String siteName,
            @RequestParam(required = false, defaultValue = "부적합한 스팸성 내용") String title,
            @RequestParam(required = false, defaultValue = "해당 게시글은 광고 목적의 스팸 게시물로 판단됩니다.\n커뮤니티 규칙에 위반되는 내용이 포함되어 있어 신고합니다.") String content,
            Model model
    ) {
        // 미리보기 샘플 데이터 설정
        model.addAttribute("from", from);
        model.addAttribute("siteName", siteName);
        model.addAttribute("title", title);
        model.addAttribute("content", content);
        return "mail/send_to_report";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/send-to-helpdesk")
    public String sendToHelpdesk(
            @RequestParam(required = false, defaultValue = "example@email.com") String from,
            @RequestParam(required = false, defaultValue = "대전포스트잇 ♣대전독서모임") String siteName,
            @RequestParam(required = false, defaultValue = "이용 방법 문의") String title,
            @RequestParam(required = false, defaultValue = "안녕하세요. 서비스 이용 중 궁금한 점이 있어 문의드립니다.\n게시글 작성 시 첨부파일 용량 제한이 어떻게 되는지 알고 싶습니다.") String content,
            Model model
    ) {
        // 미리보기 샘플 데이터 설정
        model.addAttribute("from", from);
        model.addAttribute("siteName", siteName);
        model.addAttribute("title", title);
        model.addAttribute("content", content);
        return "mail/send_to_helpdesk";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/article-created")
    public String articleCreated(
            @RequestParam(required = false, defaultValue = "새 게시글 제목 미리보기") String title,
            @RequestParam(required = false, defaultValue = "새 게시글 내용 미리보기입니다. HTML 태그가 제거되고 100자 이내로 절삭됩니다...") String content,
            @RequestParam(required = false, defaultValue = "http://localhost:8080/articles/sample-article-id?categoryId=sample-category-id") String articleUrl,
            Model model
    ) {
        model.addAttribute("title", title);
        model.addAttribute("content", content);
        model.addAttribute("articleUrl", articleUrl);
        return "mail/article_created";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/reply-created")
    public String replyCreated(
            @RequestParam(required = false, defaultValue = "새 댓글이 달린 게시글 제목 미리보기") String title,
            @RequestParam(required = false, defaultValue = "새 댓글 내용 미리보기입니다. HTML 태그가 제거되고 100자 이내로 절삭됩니다...") String content,
            @RequestParam(required = false, defaultValue = "http://localhost:8080/articles/sample-article-id?categoryId=sample-category-id#sample-reply-id") String articleUrl,
            Model model
    ) {
        model.addAttribute("title", title);
        model.addAttribute("content", content);
        model.addAttribute("articleUrl", articleUrl);
        return "mail/reply_created";
    }

}

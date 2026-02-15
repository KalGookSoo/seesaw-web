package kr.me.seesaw.service;

import kr.me.seesaw.domain.Article;
import kr.me.seesaw.domain.VEvent;
import kr.me.seesaw.dto.command.CreateEventCommand;
import kr.me.seesaw.dto.command.UpdateEventCommand;
import kr.me.seesaw.dto.model.VEventModel;
import kr.me.seesaw.dto.query.EventQuery;
import kr.me.seesaw.model.ArticleModel;
import kr.me.seesaw.repository.ArticleRepository;
import kr.me.seesaw.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultEventWebService implements EventWebService {

    private final EventRepository eventRepository;

    private final ArticleService articleService;

    private final ArticleRepository articleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VEventModel> findAll(EventQuery search) {
        return eventRepository.findAll(search).stream()
                .map(VEventModel::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VEventModel find(String id) {
        VEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));
        VEventModel model = new VEventModel(event);

        // Article 상세 정보(첨부파일, 댓글 등)를 포함하기 위해 ArticleModel 상세 조회를 활용할 수 있음
        ArticleModel articleAggregation = articleService.getArticleAggregation(event.getArticleId());
        model.setArticle(articleAggregation);

        return model;
    }

    @Override
    @Transactional
    public VEventModel create(CreateEventCommand command, List<MultipartFile> files) throws IOException {
        // 1. Article 생성 (이미 첨부파일 및 위지윅 본문 처리가 포함된 articleService 활용)
        ArticleModel articleModel = articleService.create(command.getArticle());
        Article article = articleRepository.findById(articleModel.getId())
                .orElseThrow(() -> new IllegalStateException("Article creation failed"));

        // 2. VEvent 생성 및 Article 연동
        VEvent event = new VEvent();
        event.setArticle(article);
        event.setDtStart(command.getDtStart());
        event.setDtEnd(command.getDtEnd());
        event.setSummary(command.getSummary());
        event.setDescription(command.getDescription());
        event.setLocation(command.getLocation());
        event.setStatus(command.getStatus());
        event.setTzid(command.getTzid());
        event.setDtStamp(Instant.now());

        // Article 본문과 Event description 동기화 (필요시)
        if (event.getDescription() == null || event.getDescription().isBlank()) {
            event.setDescription(article.getContent());
        }

        eventRepository.save(event);

        return find(article.getId());
    }

    @Override
    @Transactional
    public VEventModel update(String id, UpdateEventCommand command, List<MultipartFile> files) throws IOException {
        VEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));

        // 1. Article 수정
        articleService.update(event.getArticleId(), command.getArticle());

        // 2. VEvent 수정
        event.setDtStart(command.getDtStart());
        event.setDtEnd(command.getDtEnd());
        event.setSummary(command.getSummary());
        event.setDescription(command.getDescription());
        event.setLocation(command.getLocation());
        event.setStatus(command.getStatus());
        event.setTzid(command.getTzid());
        event.setDtStamp(Instant.now());

        return find(id);
    }

    @Override
    @Transactional
    public void delete(String id) {
        VEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));

        // Article 삭제 시 연관된 VEvent도 연쇄 삭제되도록 구성되어 있거나 수동 삭제
        articleService.delete(event.getArticleId());
        eventRepository.delete(event);
    }

}

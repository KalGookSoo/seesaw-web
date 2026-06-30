package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateArticleCommand;
import kr.me.seesaw.command.UpdateArticleCommand;
import kr.me.seesaw.domain.Article;
import kr.me.seesaw.domain.VEvent;
import kr.me.seesaw.domain.vo.ArticleType;
import kr.me.seesaw.dto.command.CreateEventCommand;
import kr.me.seesaw.dto.command.UpdateEventCommand;
import kr.me.seesaw.dto.model.VEventModel;
import kr.me.seesaw.dto.query.EventQuery;
import kr.me.seesaw.model.ArticleModel;
import kr.me.seesaw.repository.ArticleRepository;
import kr.me.seesaw.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class DefaultEventWebService implements EventWebService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventRepository eventRepository;

    private final ArticleService articleService;

    private final ArticleRepository articleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VEventModel> findAll(EventQuery search) {
        logger.debug("이벤트 목록 조회: search={}", search);
        return eventRepository.findAll(search).stream()
                .map(VEventModel::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VEventModel find(String id) {
        logger.debug("이벤트 상세 조회: id={}", id);
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
    public VEventModel create(CreateEventCommand command) throws IOException {
        logger.info("이벤트 생성: command={}", command);

        // 1. Article 생성
        CreateArticleCommand articleCommand = new CreateArticleCommand();
        articleCommand.setCategoryId(command.getCategoryId());
        articleCommand.setTitle(command.getTitle());
        articleCommand.setContent(command.getContent());
        articleCommand.setType(ArticleType.HTML);
        articleCommand.setFixed(false);
        articleCommand.setFixedOrder(null);
        articleCommand.setMultipartFiles(command.getMultipartFiles());
        articleCommand.setInlineImages(command.getInlineImages());

        ArticleModel articleModel = articleService.create(articleCommand);
        Article article = articleRepository.getReferenceById(articleModel.getId());

        // 2. VEvent 생성 및 Article 연동
        VEvent event = new VEvent();
        event.setArticle(article);
        event.setDtStart(command.getDtStart());
        event.setDtEnd(command.getDtEnd());
        event.setSummary(command.getTitle());
        event.setLocation(command.getLocation());
        event.setStatus(command.getStatus());
        event.setTzid(command.getTzid());
        event.setDtStamp(Instant.now());

        eventRepository.save(event);

        VEventModel model = new VEventModel(event);
        model.setArticle(articleModel);
        model.setDescription(articleModel.getContent());
        return model;
    }

    @Override
    @Transactional
    public VEventModel update(String id, UpdateEventCommand command) throws IOException {
        logger.info("이벤트 수정: id={}, command={}", id, command);
        VEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));

        // 1. Article 수정
        UpdateArticleCommand articleCommand = new UpdateArticleCommand();
        articleCommand.setCategoryId(command.getCategoryId() != null ? command.getCategoryId() : event.getArticle().getCategory().getId());
        articleCommand.setTitle(command.getTitle());
        articleCommand.setContent(command.getContent());
        articleCommand.setType(ArticleType.HTML);
        articleCommand.setFixed(false);
        articleCommand.setFixedOrder(null);
        articleCommand.setMultipartFiles(command.getMultipartFiles());
        articleCommand.setInlineImages(command.getInlineImages());

        ArticleModel articleModel = articleService.update(event.getArticleId(), articleCommand);

        // 2. VEvent 수정
        event.setDtStart(command.getDtStart());
        event.setDtEnd(command.getDtEnd());
        event.setSummary(command.getTitle());
        event.setLocation(command.getLocation());
        if (command.getStatus() != null) {
            event.setStatus(command.getStatus());
        }
        if (command.getTzid() != null) {
            event.setTzid(command.getTzid());
        }
        event.setDtStamp(Instant.now());

        VEvent updatedEvent = eventRepository.update(event);

        VEventModel model = new VEventModel(updatedEvent);
        model.setArticle(articleModel);
        model.setDescription(articleModel.getContent());
        return model;
    }

    @Override
    @Transactional
    public void delete(String id) {
        logger.info("이벤트 삭제: id={}", id);
        VEvent event = eventRepository.getReferenceById(id);
        eventRepository.delete(event);
        articleService.delete(event.getArticleId());
    }

}

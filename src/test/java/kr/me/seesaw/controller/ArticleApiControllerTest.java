package kr.me.seesaw.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.me.seesaw.command.CreateArticleCommand;
import kr.me.seesaw.domain.vo.ArticleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ArticleApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .registerModule(new JavaTimeModule());

    @Test
    @DisplayName("게시글 생성 - 요청 권한 없음(403)")
    void createArticleCase1() throws Exception {

        CreateArticleCommand command = new CreateArticleCommand();
        String categoryId = "071e581d-4fbb-46fd-9d5a-fdb69d98ae75";// "업데이트" 카테고리
        command.setCategoryId(categoryId);
        command.setType(ArticleType.HTML);
        command.setFixed(false);
        command.setFixedOrder(1);
        command.setTitle("Test Title");
        command.setContent("Test Content");

        MockMultipartFile file = new MockMultipartFile("multipartFiles", "file.txt", MediaType.TEXT_PLAIN_VALUE, "file content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/articles")
                        .file(file)
                        .param("categoryId", command.getCategoryId())
                        .param("type", command.getType().name())
                        .param("fixed", String.valueOf(command.isFixed()))
                        .param("fixedOrder", String.valueOf(command.getFixedOrder()))
                        .param("title", command.getTitle())
                        .param("content", command.getContent())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("게시글 생성 - 관리자 인증 및 커맨드 검증 실패(400)")
    void createArticleCase2() throws Exception {

        CreateArticleCommand command = new CreateArticleCommand();
        String invalidCategoryId = "";
        command.setCategoryId(invalidCategoryId);
        command.setType(ArticleType.HTML);
        command.setFixed(false);
        command.setFixedOrder(1);
        command.setTitle("Test Title");
        command.setContent("Test Content");

        MockMultipartFile file = new MockMultipartFile("multipartFiles", "file.txt", MediaType.TEXT_PLAIN_VALUE, "file content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/articles")
                        .file(file)
                        .param("categoryId", command.getCategoryId())
                        .param("type", command.getType().name())
                        .param("fixed", String.valueOf(command.isFixed()))
                        .param("fixedOrder", String.valueOf(command.getFixedOrder()))
                        .param("title", command.getTitle())
                        .param("content", command.getContent())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("게시글 생성 - 관리자 인증 및 커맨드 검증 성공(200)")
    void createArticleCase3() throws Exception {

        CreateArticleCommand command = new CreateArticleCommand();
        String categoryId = "071e581d-4fbb-46fd-9d5a-fdb69d98ae75";// "업데이트" 카테고리
        command.setCategoryId(categoryId);
        command.setType(ArticleType.HTML);
        command.setFixed(false);
        command.setFixedOrder(1);
        command.setTitle("Test Title");
        command.setContent("Test Content");

        MockMultipartFile file = new MockMultipartFile("multipartFiles", "file.txt", MediaType.TEXT_PLAIN_VALUE, "file content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/articles")
                        .file(file)
                        .param("categoryId", command.getCategoryId())
                        .param("type", command.getType().name())
                        .param("fixed", String.valueOf(command.isFixed()))
                        .param("fixedOrder", String.valueOf(command.getFixedOrder()))
                        .param("title", command.getTitle())
                        .param("content", command.getContent())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @SuppressWarnings("DefaultAnnotationParam")
    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    @DisplayName("게시글 생성 - 사용자 인증 및 커맨드 검증 성공(200)")
    void createArticleCase4() throws Exception {

        CreateArticleCommand command = new CreateArticleCommand();
        String categoryId = "071e581d-4fbb-46fd-9d5a-fdb69d98ae75";// "업데이트" 카테고리
        command.setCategoryId(categoryId);
        command.setType(ArticleType.HTML);
        command.setFixed(false);
        command.setFixedOrder(1);
        command.setTitle("Test Title");
        command.setContent("Test Content");

        MockMultipartFile file = new MockMultipartFile("multipartFiles", "file.txt", MediaType.TEXT_PLAIN_VALUE, "file content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/articles")
                        .file(file)
                        .param("categoryId", command.getCategoryId())
                        .param("type", command.getType().name())
                        .param("fixed", String.valueOf(command.isFixed()))
                        .param("fixedOrder", String.valueOf(command.getFixedOrder()))
                        .param("title", command.getTitle())
                        .param("content", command.getContent())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
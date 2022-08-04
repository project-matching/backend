package com.matching.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.project.dto.comment.CommentRequestDto;
import com.matching.project.dto.common.TokenDto;
import com.matching.project.dto.enumerate.EmailAuthPurpose;
import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.user.EmailAuthRequestDto;
import com.matching.project.entity.Comment;
import com.matching.project.entity.EmailAuth;
import com.matching.project.entity.Project;
import com.matching.project.entity.User;
import com.matching.project.repository.*;
import com.matching.project.service.EmailService;
import com.matching.project.service.JwtTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class CommentControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    CommentRepository commentRepository;

    User saveUser() {
        User user = User.builder()
                .name("testUser")
                .sex("M")
                .email("leeworld9@naver.com")
                .password(passwordEncoder.encode("test"))
                .github(null)
                .selfIntroduction(null)
                .block(false)
                .blockReason(null)
                .permission(Role.ROLE_USER)
                .oauthCategory(OAuth.NORMAL)
                .email_auth(false)
                .imageNo(null)
                .position(null)
                .build();
        return userRepository.save(user);
    }

    Project saveProject(User user) {
        Project project = Project.builder()
                .name("testProject")
                .createUserName(user.getName())
                .createDate(LocalDateTime.now())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(5))
                .state(true)
                .introduction(null)
                .maxPeople(4)
                .currentPeople(1)
                .delete(false)
                .deleteReason(null)
                .viewCount(0)
                .commentCount(0)
                .build();
        return projectRepository.save(project);
    }

    Comment saveComment(User user, Project project) {
        Comment comment = Comment.builder()
                .project(project)
                .content("testComment")
                .user(user)
                .build();
        return commentRepository.save(comment);
    }

    String getToken(User user) {
        return jwtTokenService.createToken(TokenDto.builder().email(user.getEmail()).build());
    }

    @Nested
    @DisplayName("댓글 등록")
    class commentRegister {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveUser();
            String token = getToken(user);
            Project project = saveProject(user);

            CommentRequestDto requestDto = CommentRequestDto.builder()
                    .content("testComment")
                    .build();

            //when
            ResultActions resultActions = mvc.perform(post("/v1/comment/" + project.getNo())
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(requestDto))
            );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk());
            List<Comment> comment = commentRepository.findAll();
            assertThat(comment.get(0).getProject().getNo()).isEqualTo(project.getNo());
            assertThat(comment.get(0).getContent()).isEqualTo(requestDto.getContent());
            assertThat(comment.get(0).getUser().getName()).isEqualTo(user.getName());
        }

        @DisplayName("실패 : 로그인 하지 않는 경우")
        @Test
        void fail() throws Exception {
            //given
            User user = saveUser();
            Project project = saveProject(user);

            CommentRequestDto requestDto = CommentRequestDto.builder()
                    .content("testComment")
                    .build();

            //when
            ResultActions resultActions = mvc.perform(post("/v1/comment/" + project.getNo())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(requestDto))
            );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("댓글 조회")
    class commentList {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveUser();
            String token = getToken(user);
            Project project = saveProject(user);
            Comment comment = saveComment(user, project);

            //when
            ResultActions resultActions = mvc.perform(get("/v1/comment/" + project.getNo())
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.[0].commentNo").value(comment.getNo()))
                    .andExpect(jsonPath("$.data.[0].userNo").value(comment.getUser().getNo()))
                    .andExpect(jsonPath("$.data.[0].registrant").value(comment.getUser().getName()))
                    .andExpect(jsonPath("$.data.[0].content").value(comment.getContent()));
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class commentUpdate {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveUser();
            String token = getToken(user);
            Project project = saveProject(user);
            Comment comment = saveComment(user, project);

            CommentRequestDto requestDto = CommentRequestDto.builder()
                    .content("updateComment")
                    .build();

            //when
            ResultActions resultActions = mvc.perform(patch("/v1/comment/" + comment.getNo())
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(requestDto))
            );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk());
            assertThat(comment.getProject().getNo()).isEqualTo(project.getNo());
            assertThat(comment.getContent()).isEqualTo(requestDto.getContent());
            assertThat(comment.getUser().getName()).isEqualTo(user.getName());
        }

        @DisplayName("실패 : 로그인 하지 않는 경우")
        @Test
        void fail() throws Exception {
            //given
            User user = saveUser();
            Project project = saveProject(user);
            Comment comment = saveComment(user, project);

            CommentRequestDto requestDto = CommentRequestDto.builder()
                    .content("updateComment")
                    .build();

            //when
            ResultActions resultActions = mvc.perform(patch("/v1/comment/" + comment.getNo())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(requestDto))
            );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class commentDelete {

        @DisplayName("성공")
        @Test
        void success() throws Exception {
            //given
            User user = saveUser();
            String token = getToken(user);
            Project project = saveProject(user);
            Comment comment = saveComment(user, project);

            //when
            ResultActions resultActions = mvc.perform(delete("/v1/comment/" + comment.getNo())
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isOk());
            List<Comment> commentList = commentRepository.findAll();
            assertThat(commentList.size()).isEqualTo(0);
        }

        @DisplayName("실패 : 로그인 하지 않는 경우")
        @Test
        void fail() throws Exception {
            //given
            User user = saveUser();
            Project project = saveProject(user);
            Comment comment = saveComment(user, project);

            //when
            ResultActions resultActions = mvc.perform(delete("/v1/comment/" + comment.getNo())
                    .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }
}
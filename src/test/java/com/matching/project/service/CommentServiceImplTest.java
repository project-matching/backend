package com.matching.project.service;

import com.matching.project.dto.SliceDto;
import com.matching.project.dto.comment.CommentDto;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.Comment;
import com.matching.project.entity.Project;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.repository.CommentRepository;
import com.matching.project.repository.ProjectRepository;
import com.matching.project.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Nested
    @DisplayName("댓글 조회")
    class CommentInfo {

        @DisplayName("성공")
        @Test
        void success() {
            Long userNo = 1L;
            String userName = "테스터";
            String userEmail = "leeworld9@gmail.com";
            Role userRole = Role.ROLE_USER;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            Long projectNo = 1L;
            String projectName = "testProject";
            Project project = Project.builder()
                    .no(projectNo)
                    .name(projectName)
                    .createUserName(user.getName())
                    .build();

            given(projectRepository.findById(projectNo)).willReturn(Optional.ofNullable(project));

            String commentContent = "코멘트 테스트";
            Long commentNo = 3L;

            List<Comment> commentList = new ArrayList<>();
            for (int i = 0 ; i < 10 ; i++) {
                commentList.add(Comment.builder()
                        .no(Integer.toUnsignedLong(i+1))
                        .user(user)
                        .project(project)
                        .content(commentContent + Integer.toUnsignedLong(i+1))
                        .build());
            }

            int page = 0;
            int size = 3;
            Pageable pageable = PageRequest.of(page, size, Sort.by("no").descending());
            int start = Math.toIntExact(commentNo) - 1;
            int end = Math.min((start + pageable.getPageSize()), commentList.size());
            boolean hasNext = commentList.size() >= start + pageable.getPageSize() + 1;
            Slice<Comment> comments = new SliceImpl<>(commentList.subList(start, end), pageable, hasNext);

            given(commentRepository.findByProjectOrderByNoDescUsingPaging(project, commentNo, pageable))
                    .willReturn(comments);

            //when
            SliceDto<CommentDto> commentDtoList = commentService.commentList(projectNo, commentNo, pageable);

            //then
            assertThat(commentDtoList.getContent().get(0).getCommentNo()).isEqualTo(Integer.toUnsignedLong(3));
            assertThat(commentDtoList.getContent().get(0).getContent()).isEqualTo(commentContent + 3);
            assertThat(commentDtoList.getContent().get(1).getCommentNo()).isEqualTo(Integer.toUnsignedLong(4));
            assertThat(commentDtoList.getContent().get(1).getContent()).isEqualTo(commentContent + 4);
            assertThat(commentDtoList.getContent().get(2).getCommentNo()).isEqualTo(Integer.toUnsignedLong(5));
            assertThat(commentDtoList.getContent().get(2).getContent()).isEqualTo(commentContent + 5);
            assertThat(commentDtoList.isLast()).isFalse();


        }
        @DisplayName("실패 : 존재하지 않는 프로젝트")
        @Test
        void fail1() {
            Long userNo = 1L;
            String userName = "테스터";
            String userEmail = "leeworld9@gmail.com";
            Role userRole = Role.ROLE_USER;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            Long projectNo = 1L;
            String projectName = "testProject";

            given(projectRepository.findById(projectNo)).willReturn(Optional.empty());

            int page = 0;
            int size = 1;
            Pageable pageable = PageRequest.of(page, size, Sort.by("no").descending());

            //when
            CustomException e = Assertions.assertThrows(CustomException.class, () -> {
                SliceDto<CommentDto> commentList = commentService.commentList(projectNo, null, pageable);
            });

            //then
            assertThat(e.getErrorCode().getDetail()).isEqualTo("Not Find Project No");

        }
    }

    @Nested
    @DisplayName("댓글 등록")
    class CommentRegister {
        @DisplayName("성공")
        @Test
        void success() {
            //given
            String commentContent = "코멘트 테스트";

            Long userNo = 1L;
            String userName = "테스터";
            String userEmail = "leeworld9@gmail.com";
            Role userRole = Role.ROLE_USER;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            Long projectNo = 1L;
            String projectName = "testProject";
            Project project = Project.builder()
                    .no(projectNo)
                    .name(projectName)
                    .createUserName(user.getName())
                    .build();

            given(projectRepository.findById(projectNo)).willReturn(Optional.ofNullable(project));
            given(userRepository.findById(user.getNo())).willReturn(Optional.ofNullable(user));

            //when
            Comment comment = commentService.commentRegister(projectNo, commentContent);

            //then
            assertThat(comment.getUser().getName()).isEqualTo(userName);
            assertThat(comment.getProject().getName()).isEqualTo(projectName);
            assertThat(comment.getContent()).isEqualTo(commentContent);
        }

        @DisplayName("실패 : 존재하지 않는 프로젝트")
        @Test
        void fail1() {
            //given
            String commentContent = "코멘트 테스트";

            Long userNo = 1L;
            String userName = "테스터";
            String userEmail = "leeworld9@gmail.com";
            Role userRole = Role.ROLE_USER;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            Long projectNo = 1L;
            String projectName = "testProject";
            Project project = Project.builder()
                    .no(projectNo)
                    .name(projectName)
                    .createUserName(user.getName())
                    .build();

            given(userRepository.findById(user.getNo())).willReturn(Optional.ofNullable(user));
            given(projectRepository.findById(projectNo)).willReturn(Optional.empty());

            //when
            CustomException e = Assertions.assertThrows(CustomException.class, () -> {
                Comment comment = commentService.commentRegister(projectNo, commentContent);
            });

            //then
            assertThat(e.getErrorCode().getDetail()).isEqualTo("Not Find Project No");

        }

    }

    @Nested
    @DisplayName("댓글 수정")
    class CommentUpdate {

        @DisplayName("성공")
        @Test
        void success() {
            //given
            String updateContent = "코멘트 업데이트";

            Long userNo = 1L;
            String userName = "테스터";
            String userEmail = "leeworld9@gmail.com";
            Role userRole = Role.ROLE_USER;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            Long commentNo = 1L;
            String commentContent = "코멘트 테스트";

            Comment comment = Comment.builder()
                    .no(commentNo)
                    .content(commentContent)
                    .user(user)
                    .build();

            given(commentRepository.findById(commentNo)).willReturn(Optional.ofNullable(comment));
            given(userRepository.findById(user.getNo())).willReturn(Optional.ofNullable(user));

            //when
            Comment resComment = commentService.commentUpdate(commentNo, updateContent);

            //then
            assertThat(resComment.getNo()).isEqualTo(commentNo);
            assertThat(resComment.getContent()).isEqualTo(updateContent);
        }

        @DisplayName("실패 : 존재하지 않는 댓글에 접근")
        @Test
        void fail1() {
            //given
            String updateContent = "코멘트 업데이트";

            Long userNo1 = 1L;
            String userName1 = "테스터";
            String userEmail1 = "leeworld9@gmail.com";
            Role userRole1 = Role.ROLE_USER;

            User user = User.builder()
                    .no(userNo1)
                    .name(userName1)
                    .email(userEmail1)
                    .permission(userRole1)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            Long commentNo = 1L;
            String commentContent = "코멘트 테스트";

            given(userRepository.findById(user.getNo())).willReturn(Optional.ofNullable(user));

            //when
            CustomException e = Assertions.assertThrows(CustomException.class, () -> {
                Comment resComment = commentService.commentUpdate(commentNo, commentContent);
            });

            //then
            assertThat(e.getErrorCode().getDetail()).isEqualTo("Not Find Comment No");
        }


        @DisplayName("실패 : 허용되지 않은 사용자가 접근")
        @Test
        void fail2() {
            //given
            String updateContent = "코멘트 업데이트";

            Long userNo1 = 1L;
            String userName1 = "테스터";
            String userEmail1 = "leeworld9@gmail.com";
            Role userRole1 = Role.ROLE_USER;

            User user = User.builder()
                    .no(userNo1)
                    .name(userName1)
                    .email(userEmail1)
                    .permission(userRole1)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            Long commentNo = 1L;
            String commentContent = "코멘트 테스트";

            Long userNo2 = 2L;
            String userName2 = "테스터2";
            String userEmail2 = "test@gmail.com";
            Role userRole2 = Role.ROLE_USER;

            User defUser = User.builder()
                    .no(userNo2)
                    .name(userName2)
                    .email(userEmail2)
                    .permission(userRole2)
                    .build();

            Comment comment = Comment.builder()
                    .no(commentNo)
                    .content(commentContent)
                    .user(defUser)
                    .build();

            given(commentRepository.findById(commentNo)).willReturn(Optional.ofNullable(comment));
            given(userRepository.findById(user.getNo())).willReturn(Optional.ofNullable(user));

            //when
            CustomException e = Assertions.assertThrows(CustomException.class, () -> {
                Comment resComment = commentService.commentUpdate(commentNo, commentContent);
            });

            //then
            assertThat(e.getErrorCode().getDetail()).isEqualTo("Unauthorized User Access");
        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class CommentDelete {

        @DisplayName("성공")
        @Test
        void Success() {
            //given
            Long userNo = 1L;
            String userName = "테스터";
            String userEmail = "leeworld9@gmail.com";
            Role userRole = Role.ROLE_USER;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            Long commentNo = 1L;
            String commentContent = "코멘트 테스트";

            Comment comment = Comment.builder()
                    .no(commentNo)
                    .content(commentContent)
                    .user(user)
                    .build();

            given(commentRepository.findById(commentNo)).willReturn(Optional.ofNullable(comment));
            given(userRepository.findById(user.getNo())).willReturn(Optional.ofNullable(user));

            //when
            commentService.commentDelete(commentNo);

            //verify
            verify(commentRepository, times(1)).deleteById(commentNo);
        }

        @DisplayName("실패 : 존재하지 않는 댓글에 접근")
        @Test
        void fail1() {
            //given
            Long userNo = 1L;
            String userName = "테스터";
            String userEmail = "leeworld9@gmail.com";
            Role userRole = Role.ROLE_USER;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            Long commentNo = 1L;
            String commentContent = "코멘트 테스트";

            given(commentRepository.findById(commentNo)).willReturn(Optional.empty());
            given(userRepository.findById(user.getNo())).willReturn(Optional.ofNullable(user));

            //when
            CustomException e = Assertions.assertThrows(CustomException.class, () -> {
                commentService.commentDelete(commentNo);
            });

            //given
            assertThat(e.getErrorCode().getDetail()).isEqualTo("Not Find Comment No");
        }

        @DisplayName("실패 : 허용되지 않은 사용자가 접근")
        @Test
        void fail2() {
            //given
            Long userNo = 1L;
            String userName = "테스터";
            String userEmail = "leeworld9@gmail.com";
            Role userRole = Role.ROLE_USER;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            Long commentNo = 1L;
            String commentContent = "코멘트 테스트";

            Long userNo2 = 2L;
            String userName2 = "테스터2";
            String userEmail2 = "test@gmail.com";
            Role userRole2 = Role.ROLE_USER;

            User defUser = User.builder()
                    .no(userNo2)
                    .name(userName2)
                    .email(userEmail2)
                    .permission(userRole2)
                    .build();

            Comment comment = Comment.builder()
                    .no(commentNo)
                    .content(commentContent)
                    .user(defUser)
                    .build();

            given(commentRepository.findById(commentNo)).willReturn(Optional.ofNullable(comment));
            given(userRepository.findById(user.getNo())).willReturn(Optional.ofNullable(user));

            //when
            CustomException e = Assertions.assertThrows(CustomException.class, () -> {
                commentService.commentDelete(commentNo);
            });

            //given
            assertThat(e.getErrorCode().getDetail()).isEqualTo("Unauthorized User Access");
        }
    }
}
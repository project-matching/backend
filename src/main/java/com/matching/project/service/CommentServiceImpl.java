package com.matching.project.service;

import com.matching.project.dto.comment.CommentDto;
import com.matching.project.entity.Comment;
import com.matching.project.entity.Project;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.CommentRepository;
import com.matching.project.repository.ProjectRepository;
import com.matching.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final ProjectRepository projectRepository;
    private final CommentRepository commentRepository;

    public void commentValidCheck(User user, Comment comment) {
        // 본인만 수정, 삭제가 가능하여야 함
        if (!comment.getUser().getName().equals(user.getName()))
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER_ACCESS_EXCEPTION);
    }

    @Override
    public List<CommentDto> commentList(Pageable pageable, Long projectNo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = Optional.ofNullable((User)auth.getPrincipal());

        Optional<Project> optionalProject = projectRepository.findById(projectNo);
        optionalProject.orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_PROJECT_NO_EXCEPTION));

        List<Comment> commentPage = commentRepository.findByProjectNoUsingPaging(pageable, optionalProject.get());

        return commentPage.stream().map(CommentDto::toCommentDto).collect(Collectors.toList());
    }

    @Override
    public Comment commentRegister(Long projectNo, String content) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = Optional.ofNullable((User)auth.getPrincipal());

        Optional<Project> optionalProject = projectRepository.findById(projectNo);
        optionalProject.orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_PROJECT_NO_EXCEPTION));

        Comment comment = Comment.builder()
                .user(optionalUser.get())
                .project(optionalProject.get())
                .content(content)
                .build();

        // Comment Save
        commentRepository.save(comment);

        return comment;
    }

    @Transactional
    @Override
    public Comment commentUpdate(Long commentNo, String content) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = Optional.ofNullable((User)auth.getPrincipal());

        Optional<Comment> optionalComment = commentRepository.findById(commentNo);
        optionalComment.orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_COMMENT_NO_EXCEPTION));

        // Comment Valid Check
        commentValidCheck(optionalUser.get(), optionalComment.get());

        // Comment Update
        optionalComment.get().updateComment(content);

        return optionalComment.get();
    }

    @Transactional
    @Override
    public void commentDelete(Long commentNo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = Optional.ofNullable((User)auth.getPrincipal());

        Optional<Comment> optionalComment = commentRepository.findById(commentNo);
        optionalComment.orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_COMMENT_NO_EXCEPTION));

        // Comment Valid Check
        commentValidCheck(optionalUser.get(), optionalComment.get());

        // Comment Delete
        commentRepository.deleteById(commentNo);
    }
}

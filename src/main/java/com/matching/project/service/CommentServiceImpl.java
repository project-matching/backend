package com.matching.project.service;

import com.matching.project.dto.SliceDto;
import com.matching.project.dto.comment.CommentDto;
import com.matching.project.entity.Comment;
import com.matching.project.entity.Project;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.CommentRepository;
import com.matching.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
    public SliceDto<CommentDto> commentList(Long projectNo, Long commentNo, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = Optional.ofNullable((User)auth.getPrincipal());

        Optional<Project> optionalProject = projectRepository.findById(projectNo);
        optionalProject.orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_PROJECT_NO_EXCEPTION));

        if (commentNo == null)
            commentNo = Long.MAX_VALUE;

        Slice<Comment> commentPage = commentRepository.findByProjectOrderByNoDescUsingPaging(optionalProject.get(), commentNo, pageable);

        SliceDto<CommentDto> dto = SliceDto.<CommentDto>builder()
                .content(commentPage.stream().
                        map(CommentDto::toCommentDto)
                        .collect(Collectors.toList()))
                .last(commentPage.isLast())
                .build();
        return dto;
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

package com.matching.project.service;

import com.matching.project.dto.SliceDto;
import com.matching.project.dto.project.ProjectSimpleDto;
import com.matching.project.entity.BookMark;
import com.matching.project.entity.Project;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.BookMarkRepository;
import com.matching.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookMarkServiceImpl implements BookMarkService{
    private final ProjectRepository projectRepository;
    private final BookMarkRepository bookMarkRepository;

    @Override
    public boolean bookMarkRegister(Long projectNo) throws Exception {
        User user = getUser();
        Project project = projectRepository.findById(projectNo).orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_PROJECT_EXCEPTION));

        BookMark bookMark = BookMark
                .builder()
                .user(user)
                .project(project).build();

        bookMarkRepository.save(bookMark);

        return true;
    }

    @Override
    public SliceDto<ProjectSimpleDto> findBookMarkProject(Long projectNo, Pageable pageable) throws Exception {
        Slice<ProjectSimpleDto> projectSimpleDtoSlice = projectRepository.findBookMarkProject(pageable, projectNo, getUser())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FIND_PROJECT_EXCEPTION));

        return new SliceDto<>(projectSimpleDtoSlice.getContent(), projectSimpleDtoSlice.isLast());
    }

    @Override
    public boolean bookMarkDelete(Long projectNo) throws Exception {
        bookMarkRepository.deleteByProjectNo(projectNo);

        return true;
    }

    // 현재 로그인한 유저 정보 가져오기
    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        return (User) principal;
    }
}

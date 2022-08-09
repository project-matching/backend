package com.matching.project.service;

import com.matching.project.dto.project.ProjectSimpleDto;
import com.matching.project.entity.BookMark;
import com.matching.project.entity.Project;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.BookMarkRepository;
import com.matching.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookMarkServiceImpl implements BookMarkService{
    private final ProjectRepository projectRepository;
    private final BookMarkRepository bookMarkRepository;

    @Override
    public boolean bookMarkRegister(Long projectNo) throws Exception {
        User user = getUser();
        Project project = projectRepository.findById(projectNo).orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NO_SUCH_ELEMENT_EXCEPTION));

        BookMark bookMark = BookMark
                .builder()
                .user(user)
                .project(project).build();

        bookMarkRepository.save(bookMark);

        return true;
    }

    @Override
    public List<ProjectSimpleDto> findBookMarkProject(Pageable pageable) throws Exception {
        return projectRepository.findBookMarkProject(pageable, getUser()).getContent();
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

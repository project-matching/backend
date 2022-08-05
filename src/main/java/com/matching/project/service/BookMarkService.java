package com.matching.project.service;

import com.matching.project.dto.project.ProjectSimpleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookMarkService {
    public boolean bookMarkRegister(Long projectNo) throws Exception;
    public List<ProjectSimpleDto> findBookMarkProject(Pageable pageable) throws Exception;
    public boolean bookMarkDelete(Long projectNo) throws Exception;
}

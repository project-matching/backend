package com.matching.project.service;

import com.matching.project.dto.project.ProjectSimpleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookMarkService {
    public boolean bookMarkRegister(Long projectNo) throws Exception;
    public Page<ProjectSimpleDto> findBookMarkProject(Pageable pageable) throws Exception;
    public boolean bookMarkDelete(Long projectNo) throws Exception;
}

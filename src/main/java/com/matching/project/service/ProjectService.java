package com.matching.project.service;

import com.matching.project.dto.project.LoginProjectSimpleDto;
import com.matching.project.dto.project.NoneLoginProjectSimpleDto;
import com.matching.project.dto.project.ProjectRegisterRequestDto;
import com.matching.project.dto.project.ProjectRegisterResponseDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProjectService {
    public ProjectRegisterResponseDto projectRegister(ProjectRegisterRequestDto projectRegisterRequestDto) throws Exception;

    public List<NoneLoginProjectSimpleDto> noneLoginProjectRecruitingList(Pageable pageable, boolean state) throws Exception;
    public List<LoginProjectSimpleDto> loginProjectRecruitingList(Pageable pageable, boolean state) throws Exception;
}

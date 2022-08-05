package com.matching.project.repository;

import com.matching.project.dto.user.UserFilterDto;
import com.matching.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepositoryCustom {
    Page<User> findByNoOrderByNoDescUsingQueryDsl(UserFilterDto userFilterDto, Pageable pageable);
}

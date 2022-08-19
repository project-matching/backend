package com.matching.project.repository;


import com.matching.project.dto.technicalstack.TechnicalStackRegisterFormResponseDto;
import com.matching.project.entity.Project;
import com.matching.project.entity.TechnicalStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Collection;
import java.util.Optional;

public interface TechnicalStackRepository extends JpaRepository<TechnicalStack, Long> {
    public Optional<List<TechnicalStack>> findByNoIn(Collection<Long> noList);

    public Optional<List<TechnicalStack>> findByNameIn(Collection<String> nameList);
}

package com.matching.project.repository;


import com.matching.project.entity.Project;
import com.matching.project.entity.TechnicalStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Collection;

public interface TechnicalStackRepository extends JpaRepository<TechnicalStack, Long> {
    public List<TechnicalStack> findByNoIn(Collection<Long> noList);
}

package com.matching.project.repository;

import com.matching.project.entity.Position;
import com.matching.project.entity.TechnicalStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TechnicalStackRepository extends JpaRepository<TechnicalStack, Long> {
    public List<TechnicalStack> findByNameIn(Collection<String> names);
}

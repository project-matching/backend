package com.matching.project.repository;

import com.matching.project.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {
    public List<Position> findByNameIn(Collection<String> names);
}

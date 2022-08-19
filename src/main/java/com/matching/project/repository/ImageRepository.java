package com.matching.project.repository;

import com.matching.project.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    public Optional<List<Image>> findByNoIn(Collection<Long> nos);
}

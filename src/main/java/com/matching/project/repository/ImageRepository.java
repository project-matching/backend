package com.matching.project.repository;

import com.matching.project.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    public List<Image> findByNoIn(Collection<Long> nos);
}

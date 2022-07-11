package com.matching.project.repository;

import com.matching.project.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c join fetch c.user u join fetch c.project p where p.no = :projectNo")
    public List<Comment> findByProjectNo(@Param("projectNo") Long projectNo);
}

package com.matching.project.repository;

import com.matching.project.entity.Comment;
import com.matching.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    //@Query("select c from Comment c join fetch c.user u join fetch c.project p where p = :project")
    //List<Comment> findByProjectNo(@Param("project") Project project);

    @Query("select c from Comment c join fetch c.user u join fetch c.project p where p = :project order by c.no asc")
    List<Comment> findByProjectNoUsingPaging(@Param("project") Project project, Pageable pageable);
}

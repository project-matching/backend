package com.matching.project.repository;

import com.matching.project.entity.Comment;
import com.matching.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c " +
            "join fetch c.user u " +
            "join fetch c.project p " +
            "where p = :project and c.no <= :commentNo order by c.no desc")
    Slice<Comment> findByProjectOrderByNoDescUsingPaging(@Param("project") Project project,
                                                         @Param("commentNo") Long commentNo,
                                                         Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Comment c where c.project.no = :projectNo")
    public void deleteByProjectNo(@Param("projectNo") Long projectNo);
}

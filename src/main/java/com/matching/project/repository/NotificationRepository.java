package com.matching.project.repository;

import com.matching.project.entity.Comment;
import com.matching.project.entity.Notification;
import com.matching.project.entity.Project;
import com.matching.project.entity.User;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("select n from Notification n left join fetch n.user where n.no = ?1")
    Optional<Notification> findByNoWithUserUsingLeftFetchJoin(Long notificationNo);

    @Query("select n from Notification n " +
            "left join fetch n.user u " +
            "where (u = :user or u IS NULL) " +
            "and n.no <= :notificationNo order by n.no desc")
    Optional<Slice<Notification>> findByUserOrUserIsNullOrderByNoDescUsingPaging(@Param("user") User user,
                                                                       @Param("notificationNo") Long notificationNo,
                                                                       Pageable pageable);
}

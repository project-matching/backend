package com.matching.project.repository;

import com.matching.project.entity.ParticipateRequestTechnicalStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipateRequestTechnicalStackRepository extends JpaRepository<ParticipateRequestTechnicalStack, Long>, ParticipateRequestTechnicalStackRepositoryCustom {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ParticipateRequestTechnicalStack prts where prts.projectParticipateRequest.no = :projectParticipateNo")
    void deleteByProjectParticipateNo(@Param("projectParticipateNo") Long projectParticipateNo);
}

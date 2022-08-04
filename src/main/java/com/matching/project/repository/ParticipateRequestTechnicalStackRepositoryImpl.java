package com.matching.project.repository;

import com.matching.project.entity.QParticipateRequestTechnicalStack;
import com.matching.project.entity.QProject;
import com.matching.project.entity.QProjectParticipateRequest;
import com.matching.project.entity.QProjectPosition;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.matching.project.entity.QParticipateRequestTechnicalStack.participateRequestTechnicalStack;
import static com.matching.project.entity.QProject.project;
import static com.matching.project.entity.QProjectParticipateRequest.projectParticipateRequest;
import static com.matching.project.entity.QProjectPosition.projectPosition;

@Repository
@Transactional
@RequiredArgsConstructor
public class ParticipateRequestTechnicalStackRepositoryImpl implements ParticipateRequestTechnicalStackRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    // projectNo와 관련된 participateRequestTechnicalStackRepositoryImpl 모두 삭제
    @Override
    public void deleteByProjectNo(Long projectNo) throws Exception {
        JPQLQuery<Long> subQuery = JPAExpressions.select(participateRequestTechnicalStack.no)
                .from(participateRequestTechnicalStack)
                .join(participateRequestTechnicalStack.projectParticipateRequest, projectParticipateRequest)
                .join(projectParticipateRequest.projectPosition, projectPosition)
                .join(projectPosition.project, project)
                .where(project.no.eq(projectNo));

        queryFactory.delete(participateRequestTechnicalStack)
                .where(participateRequestTechnicalStack.no.in(subQuery))
                .execute();

        entityManager.flush();
        entityManager.clear();
    }
}

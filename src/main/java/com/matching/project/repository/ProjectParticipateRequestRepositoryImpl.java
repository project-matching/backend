package com.matching.project.repository;

import com.matching.project.dto.projectparticipate.ParticipateRequestTechnicalStackDto;
import com.matching.project.dto.projectparticipate.ProjectParticipateFormResponseDto;
import com.matching.project.entity.*;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.matching.project.entity.QParticipateRequestTechnicalStack.participateRequestTechnicalStack;
import static com.matching.project.entity.QPosition.position;
import static com.matching.project.entity.QProject.project;
import static com.matching.project.entity.QProjectParticipateRequest.projectParticipateRequest;
import static com.matching.project.entity.QProjectPosition.projectPosition;
import static com.matching.project.entity.QTechnicalStack.technicalStack;
import static com.matching.project.entity.QUser.user;

@Repository
@Transactional
@RequiredArgsConstructor
public class ProjectParticipateRequestRepositoryImpl implements ProjectParticipateRequestRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    // Sort해주는 메소드
    private OrderSpecifier<?> projectParticipateRequestSort(Pageable pageable) {
        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                // 서비스에서 넣어준 DESC or ASC 를 가져온다.
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                // 서비스에서 넣어준 정렬 조건을 스위치 케이스 문을 활용하여 셋팅하여 준다.
                switch (order.getProperty()) {
                    case "no":
                        return new OrderSpecifier(direction, projectParticipateRequest.no);
                    case "createdDate":
                        return new OrderSpecifier(direction, projectParticipateRequest.createdDate);
                }
            }
        }
        return null;
    }
    
    // 신청한 프로젝트 페이지 조회
    @Override
    public Slice<ProjectParticipateFormResponseDto> findProjectParticipateRequestByProjectNo(Long projectNo, Long projectParticipateRequestNo, Pageable pageable) throws Exception {
        // projectParticipateRequest ManyToOne 조인
        List<ProjectParticipateFormResponseDto> projectParticipateFormResponseDtoList =
                queryFactory.select(Projections.constructor(ProjectParticipateFormResponseDto.class,
                        projectParticipateRequest.no,
                        user.name,
                        position.name,
                        projectParticipateRequest.motive
                ))
                .from(projectParticipateRequest)
                .join(projectParticipateRequest.projectPosition, projectPosition)
                .join(projectParticipateRequest.projectPosition.position, position)
                .join(projectParticipateRequest.user, user)
                .join(projectPosition.project, project)
                .where(
                        eqProjectNo(projectNo),
                        underProjectParticipateRequestNo(projectParticipateRequestNo))
                .offset(0)
                .limit(pageable.getPageSize() + 1)
                .orderBy(projectParticipateRequestSort(pageable))
                .fetch();

        // participateRequestTechnicalStack map 조회
        Map<Long, List<ParticipateRequestTechnicalStackDto>> participateRequestTechnicalStackMap = 
                findParticipateRequestTechnicalStackMap(toProjectParticipateRequestNo(projectParticipateFormResponseDtoList));
        
        // dto 변환
        for (ProjectParticipateFormResponseDto projectParticipateFormResponseDto : projectParticipateFormResponseDtoList) {
            List<ParticipateRequestTechnicalStackDto> participateRequestTechnicalStackDtoList = participateRequestTechnicalStackMap.get(projectParticipateFormResponseDto.getProjectParticipateNo());

            if (participateRequestTechnicalStackDtoList != null) {
                List<String> technicalStackList = participateRequestTechnicalStackDtoList.stream()
                        .map(participateRequestTechnicalStackDto -> participateRequestTechnicalStackDto.getTechnicalsTackName())
                        .collect(Collectors.toList());
                projectParticipateFormResponseDto.setTechnicalStackList(technicalStackList);
            } else {
                projectParticipateFormResponseDto.setTechnicalStackList(null);
            }
        }

        boolean hasNext = false;
        if (projectParticipateFormResponseDtoList.size() > pageable.getPageSize()) {
            projectParticipateFormResponseDtoList.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(projectParticipateFormResponseDtoList, pageable, hasNext);
    }
    
    // ProjectParticipateRequestNo 가져오기
    private List<Long> toProjectParticipateRequestNo(List<ProjectParticipateFormResponseDto> projectParticipateFormResponseDtoList) {
        return projectParticipateFormResponseDtoList.stream()
                .map(projectParticipateFormResponseDto -> projectParticipateFormResponseDto.getProjectParticipateNo())
                .collect(Collectors.toList());
    }
    
    // participateRequestTechnicalStack map 반환
    private Map<Long, List<ParticipateRequestTechnicalStackDto>> findParticipateRequestTechnicalStackMap(List<Long> projectParticipateNo) {
        List<ParticipateRequestTechnicalStackDto> participateRequestTechnicalStackDtoList =
                queryFactory.select(Projections.constructor(ParticipateRequestTechnicalStackDto.class,
                        projectParticipateRequest.no,
                        technicalStack.name))
                .from(participateRequestTechnicalStack)
                .join(participateRequestTechnicalStack.projectParticipateRequest, projectParticipateRequest)
                .join(participateRequestTechnicalStack.technicalStack, technicalStack)
                .where(projectParticipateRequest.no.in(projectParticipateNo))
                .fetch();

        return participateRequestTechnicalStackDtoList.stream()
                .collect(Collectors.groupingBy(ParticipateRequestTechnicalStackDto::getProjectParticipateNo));
    }

    private BooleanExpression eqProjectNo(Long projectNo) {
        return project.no.eq(projectNo);
    }

    private BooleanExpression underProjectParticipateRequestNo(Long projectParticipateRequestNo) {
        if (projectParticipateRequestNo == null) {
            return null;
        }
        return projectParticipateRequest.no.lt(projectParticipateRequestNo);
    }
    
    // User, projectPosition join 메소드
    @Override
    public ProjectParticipateRequest findProjectPositionAndUserAndProjectFetchJoinByNo(Long no) throws Exception {
        return queryFactory
                .selectFrom(projectParticipateRequest)
                .join(projectParticipateRequest.user, user)
                .join(projectParticipateRequest.projectPosition.project, project)
                .join(projectParticipateRequest.projectPosition, projectPosition)
                .where(projectParticipateRequest.no.eq(no))
                .fetchOne();
    }
    
    // no로 projectParticipateRequest 삭제하는 메소드
    @Override
    public long deleteByNo(Long no) throws Exception {
        long deleteNo = queryFactory.delete(projectParticipateRequest)
                .where(projectParticipateRequest.no.eq(no))
                .execute();
        entityManager.flush();
        entityManager.clear();
        
        return deleteNo;
    }
    
    // projectNo와 관련된 projectParticipateRequest 모두 삭제
    @Override
    public void deleteByProjectNo(Long projectNo) throws Exception {
        JPQLQuery<Long> subQuery = JPAExpressions.select(projectParticipateRequest.no)
                .from(projectParticipateRequest)
                .join(projectParticipateRequest.projectPosition, projectPosition)
                .join(projectPosition.project, project)
                .where(project.no.eq(projectNo));

        queryFactory.delete(projectParticipateRequest)
                .where(projectParticipateRequest.no.in(subQuery))
                .execute();
        entityManager.flush();
        entityManager.clear();
    }
}

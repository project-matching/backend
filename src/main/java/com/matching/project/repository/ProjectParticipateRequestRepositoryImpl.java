package com.matching.project.repository;

import com.matching.project.dto.projectparticipate.ParticipateRequestTechnicalStackDto;
import com.matching.project.dto.projectparticipate.ProjectParticipateFormResponseDto;
import com.matching.project.entity.*;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
public class ProjectParticipateRequestRepositoryImpl implements ProjectParticipateRequestCustom {
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
                }
            }
        }
        return null;
    }
    
    // 신청한 프로젝트 페이지 조회
    @Override
    public Page<ProjectParticipateFormResponseDto> findProjectParticipateRequestByProjectNo(Long projectNo, Pageable pageable) throws Exception {
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
                .where(eqProjectNo(projectNo))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
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

        
        // count 쿼리
        int totalSize = queryFactory.selectFrom(projectParticipateRequest)
                .join(projectParticipateRequest.projectPosition, projectPosition)
                .join(projectParticipateRequest.projectPosition.position, position)
                .join(projectParticipateRequest.user, user)
                .join(projectPosition.project, project)
                .where(eqProjectNo(projectNo))
                .fetch().size();

        return new PageImpl<>(projectParticipateFormResponseDtoList, pageable, totalSize);
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
}

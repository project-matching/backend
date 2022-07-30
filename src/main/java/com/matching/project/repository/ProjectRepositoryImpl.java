package com.matching.project.repository;

import com.matching.project.dto.enumerate.Filter;
import com.matching.project.dto.project.*;
import com.matching.project.entity.*;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.matching.project.entity.QPosition.position;
import static com.matching.project.entity.QProject.project;
import static com.matching.project.entity.QProjectPosition.projectPosition;
import static com.matching.project.entity.QProjectTechnicalStack.projectTechnicalStack;
import static com.matching.project.entity.QTechnicalStack.technicalStack;

@Repository
@Transactional
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private OrderSpecifier<?> projectSort(Pageable pageable) {
        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                // 서비스에서 넣어준 DESC or ASC 를 가져온다.
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                // 서비스에서 넣어준 정렬 조건을 스위치 케이스 문을 활용하여 셋팅하여 준다.
                switch (order.getProperty()) {
                    case "no":
                        return new OrderSpecifier(direction, project.no);
                    case "name":
                        return new OrderSpecifier(direction, project.name);
                    case "createUserName":
                        return new OrderSpecifier(direction, project.createUserName);
                    case "createDate":
                        return new OrderSpecifier(direction, project.createDate);
                    case "startDate":
                        return new OrderSpecifier(direction, project.startDate);
                    case "endDate":
                        return new OrderSpecifier(direction, project.endDate);
                    case "introduction":
                        return new OrderSpecifier(direction, project.introduction);
                    case "maxPeople":
                        return new OrderSpecifier(direction, project.maxPeople);
                    case "deleteReason":
                        return new OrderSpecifier(direction, project.deleteReason);
                    case "viewCount":
                        return new OrderSpecifier(direction, project.viewCount);
                    case "commentCount":
                        return new OrderSpecifier(direction, project.commentCount);
                }
            }
        }
        return null;
    }

    @Override
    public Page<ProjectSimpleDto> findProjectByStatus(Pageable pageable, boolean state, boolean delete, ProjectSearchRequestDto projectSearchRequestDto){
//        List<ProjectSimpleDto> projectSimpleDtoList = queryFactory.select(Projections.constructor(ProjectSimpleDto.class,
//                        project.no,
//                        project.name,
//                        project.maxPeople,
//                        project.currentPeople,
//                        project.viewCount,
//                        project.commentCount,
//                        project.createUserName))
//                .from(project)
//                .where(
//                        eqStatus(state),
//                        eqDelete(delete),
//                        search(projectSearchRequestDto))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .orderBy(projectSort(pageable))
//                .fetch();
//
//        Map<Long, List<ProjectSimplePositionDto>> projectPositionMap = findProjectPositionMap(toProjectNo(projectSimpleDtoList));
//
//        projectSimpleDtoList.forEach(
//                projectSimpleDto
//                        -> projectSimpleDto.setProjectSimplePositionDtoList(projectPositionMap.get(projectSimpleDto.getNo()
//                ))
//        );
//
//        Map<Long, List<ProjectSimpleTechnicalStackDto>> projectTechnicalStackMap = findProjectTechnicalStackMap(toProjectNo(projectSimpleDtoList));
//
//        projectSimpleDtoList.forEach(
//                projectSimpleDto
//                        -> projectSimpleDto.setProjectSimpleTechnicalStackDtoList(projectTechnicalStackMap.get(projectSimpleDto.getNo()
//                ))
//        );
//
//        int totalSize = queryFactory // count 쿼리
//                .selectFrom(project)
//                .where(project.state.eq(state).and(project.delete.eq(delete)))
//                .fetch().size();
//
//        return new PageImpl<>(projectSimpleDtoList, pageable, totalSize);
        return null;
    }

    private BooleanExpression eqStatus(boolean state) {
        return project.state.eq(state);
    }

    private BooleanExpression eqDelete(boolean delete) {
        return project.delete.eq(delete);
    }

    private BooleanExpression search(ProjectSearchRequestDto projectSearchRequestDto) {
//        if (projectSearchRequestDto == null) {
//            return null;
//        }
//
//        Filter filter = projectSearchRequestDto.getFilter();
//
//        switch (filter) {
//            case PROJECT_NAME_AND_CONTENT:
//                return project.name.contains(projectSearchRequestDto.getContent()).or(project.introduction.contains(projectSearchRequestDto.getContent()));
//        }
        return null;
    }

    private List<Long> toProjectNo(List<ProjectSimpleDto> result) {
//        return result.stream()
//                .map(projectSimpleDto -> projectSimpleDto.getNo())
//                .collect(Collectors.toList());
        return null;
    }

    private Map<Long, List<ProjectSimplePositionDto>> findProjectPositionMap(List<Long> projectNoList){
        List<ProjectSimplePositionDto> projectSimplePositionDtoList = queryFactory.select(Projections.constructor(ProjectSimplePositionDto.class,
                        project.no,
                        position.name,
                        projectPosition.state))
                .from(projectPosition)
                .join(projectPosition.project, project)
                .join(projectPosition.position, position)
                .where(project.no.in(projectNoList))
                .fetch();

        return projectSimplePositionDtoList.stream()
                .collect(Collectors.groupingBy(ProjectSimplePositionDto::getProjectNo));
    }

    private Map<Long, List<ProjectSimpleTechnicalStackDto>> findProjectTechnicalStackMap(List<Long> projectNoList){
//        List<ProjectSimpleTechnicalStackDto> projectSimplePositionDtoList = queryFactory.select(Projections.constructor(ProjectSimpleTechnicalStackDto.class,
//                        project.no,
//                        technicalStack.name))
//                .from(projectTechnicalStack)
//                .join(projectTechnicalStack.project, project)
//                .join(projectTechnicalStack.technicalStack, technicalStack)
//                .where(project.no.in(projectNoList))
//                .fetch();
//
//        return projectSimplePositionDtoList.stream()
//                .collect(Collectors.groupingBy(ProjectSimpleTechnicalStackDto::getProjectNo));
        return null;
    }
}

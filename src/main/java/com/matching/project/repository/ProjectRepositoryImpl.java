package com.matching.project.repository;

import com.matching.project.dto.enumerate.ProjectFilter;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.matching.project.entity.QImage.image;
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
    private final EntityManager em;
    
    // Sort해주는 메소드
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
    
    // 모집 상태와 삭제 상태로 검색하는 메소드
    @Override
    public Page<ProjectSimpleDto> findProjectByStatusAndDelete(Pageable pageable, boolean state, boolean delete, ProjectSearchRequestDto projectSearchRequestDto){
        List<ProjectSimpleDto> projectSimpleDtoList = queryFactory.select(Projections.constructor(ProjectSimpleDto.class,
                        project.no,
                        project.name,
                        project.maxPeople,
                        project.currentPeople,
                        project.viewCount,
                        project.createUserName))
                .from(project)
                .where(
                        eqStatus(state),
                        eqDelete(delete),
                        search(projectSearchRequestDto))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(projectSort(pageable))
                .fetch();

        Map<Long, List<ProjectSimplePositionDto>> projectPositionMap = findProjectPositionMap(toProjectNo(projectSimpleDtoList));
        
        // Dto 변환
        projectSimpleDtoList.forEach(
                projectSimpleDto
                        -> projectSimpleDto.setProjectSimplePositionDtoList(
                                projectPositionMap.get(projectSimpleDto.getProjectNo())
                )
        );

        Map<Long, List<ProjectSimpleTechnicalStackDto>> projectTechnicalStackMap = findProjectTechnicalStackMap(toProjectNo(projectSimpleDtoList));

        projectSimpleDtoList.forEach(
                projectSimpleDto
                        -> projectSimpleDto.setProjectSimpleTechnicalStackDtoList(projectTechnicalStackMap.get(projectSimpleDto.getProjectNo()
                ))
        );

        int totalSize = queryFactory // count 쿼리
                .selectFrom(project)
                .where(project.state.eq(state).and(project.delete.eq(delete)))
                .fetch().size();

        return new PageImpl<>(projectSimpleDtoList, pageable, totalSize);
    }

    private BooleanExpression eqStatus(boolean state) {
        return project.state.eq(state);
    }

    private BooleanExpression eqDelete(boolean delete) {
        return project.delete.eq(delete);
    }
    
    // 검색 메소드
    private BooleanExpression search(ProjectSearchRequestDto projectSearchRequestDto) {
        // content가 null이거나 projectSearchRequestDto가 null인 경우 null 리턴
        if (projectSearchRequestDto == null || projectSearchRequestDto.getContent() == null || projectSearchRequestDto.getContent().equals("")) {
            return null;
        }

        ProjectFilter projectFilter = projectSearchRequestDto.getProjectFilter();
        
        switch (projectFilter) {
            // PROJECT_NAME_AND_CONTENT인 경우 프로젝트 명, 내용 포함해서 검색
            case PROJECT_NAME_AND_CONTENT:
                return project.name.contains(projectSearchRequestDto.getContent()).or(project.introduction.contains(projectSearchRequestDto.getContent()));
        }
        return null;
    }
    
    // 프로젝트 번호 가져오는 메소드
    private List<Long> toProjectNo(List<ProjectSimpleDto> result) {
        return result.stream()
                .map(projectSimpleDto -> projectSimpleDto.getProjectNo())
                .collect(Collectors.toList());
    }
    
    // 프로젝트 포지션 map으로 찾는 메소드
    private Map<Long, List<ProjectSimplePositionDto>> findProjectPositionMap(List<Long> projectNoList){
        List<ProjectSimplePositionDto> projectSimplePositionDtoList = queryFactory.select(Projections.constructor(ProjectSimplePositionDto.class,
                        project.no,
                        position.no,
                        position.name))
                .from(projectPosition)
                .join(projectPosition.project, project)
                .join(projectPosition.position, position)
                .where(project.no.in(projectNoList))
                .fetch();

        return projectSimplePositionDtoList.stream()
                .collect(Collectors.groupingBy(ProjectSimplePositionDto::getProjectNo));
    }

    // 중복 제거 및 null 제거한 이미지 no 가져오는 메소드
    private List<Long> toImageNo(List<ProjectTechnicalStack> projectTechnicalStackList) {
        List<Long> result = projectTechnicalStackList.stream()
                .map(projectTechnicalStack -> projectTechnicalStack.getTechnicalStack().getImageNo()).distinct().collect(Collectors.toList());
        result.removeAll(Collections.singletonList(null));

        return result;
    }

    // 프로젝트 기술스택을 map으로 찾는 메소드
    private Map<Long, List<ProjectSimpleTechnicalStackDto>> findProjectTechnicalStackMap(List<Long> projectNoList){
        List<ProjectTechnicalStack> projectTechnicalStackList = queryFactory.selectFrom(projectTechnicalStack)
                .join(projectTechnicalStack.project, project)
                .join(projectTechnicalStack.technicalStack, technicalStack)
                .where(project.no.in(projectNoList))
                .fetch();

        // 이미지 map으로 조회
        Map<Long, Image> imageMap = findImageMap(toImageNo(projectTechnicalStackList));

        // 이미지 번호가 있는 경우에는 logicalName 삽입 없을 경우 null 삽입
        List<ProjectSimpleTechnicalStackDto> projectSimplePositionDtoList = projectTechnicalStackList.stream()
                .map(projectTechnicalStack -> new ProjectSimpleTechnicalStackDto(projectTechnicalStack.getProject().getNo(),
                        imageMap.containsKey(projectTechnicalStack.getTechnicalStack().getImageNo()) ? imageMap.get(projectTechnicalStack.getTechnicalStack().getImageNo()).getLogicalName() : null,
                        projectTechnicalStack.getTechnicalStack().getName()))
                .collect(Collectors.toList());
        
        return projectSimplePositionDtoList.stream()
                .collect(Collectors.groupingBy(ProjectSimpleTechnicalStackDto::getProjectNo));
    }
    
    // 이미지를 map으로 찾는 메소드
    private Map<Long, Image> findImageMap(List<Long> imageNoList) {
        List<Image> imageList = queryFactory
                .select(image)
                .from(image)
                .where(image.no.in(imageNoList))
                .fetch();
        return imageList.stream()
                .collect(Collectors.toMap(image -> image.getNo(), image -> image));
    }
}

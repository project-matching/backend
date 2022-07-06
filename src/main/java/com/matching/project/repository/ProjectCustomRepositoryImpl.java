package com.matching.project.repository;

import com.matching.project.dto.project.ProjectPositionQueryDto;
import com.matching.project.dto.project.ProjectQueryDto;
import com.matching.project.dto.project.ProjectTechnicalStackQueryDto;
import com.matching.project.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Repository
@Transactional
@RequiredArgsConstructor
public class ProjectCustomRepositoryImpl implements ProjectCustomRepository {
    private final EntityManager em;

    // 프로젝트 상세 정보 조회
    @Override
    public ProjectQueryDto findDetailProject(Long projectNo) {
        // projectQueryDto 찾기
        ProjectQueryDto projectQueryDto = findProjectQueryDto(projectNo);
        // projectNo를 통해 관련 position 찾기
        List<ProjectPositionQueryDto> projectPositionQueryDtoList = findProjectPositionQueryDtoList(projectQueryDto.getNo());
        // 포지션에 존재하는 기술스택을 위해 포지션 no 찾기
        List<Long> toProjectPositionNos = toProjectPositionNos(projectPositionQueryDtoList);
        Map<Long, List<ProjectTechnicalStackQueryDto>> projectTechnicalStackQueryDtoMap = findProjectTechnicalStackQueryDtoList(toProjectPositionNos);
        
        // 루프 돌면서 컬렉션 추가
        projectPositionQueryDtoList.forEach(projectPositionQueryDto -> projectPositionQueryDto.setProjectTechnicalStackList(projectTechnicalStackQueryDtoMap.get(projectPositionQueryDto.getNo())));

        // projectQueryDto에 projectPositionList 추가
        projectQueryDto.setProjectPositionList(projectPositionQueryDtoList);

        return projectQueryDto;
    }
    
    // projectQueryDto 찾는 메소드
    private ProjectQueryDto findProjectQueryDto(Long projectNo) {
        //todo 이미지 기능 추가시 수정 필요
        return em.createQuery(
                "select " +
                        "new com.matching.project.dto.project.ProjectQueryDto(p.no, p.name, p.createUserName, p.createDate, p.startDate, p.endDate, p.state, p.introduction, p.maxPeople, p.currentPeople, p.delete, p.deleteReason, p.viewCount, p.commentCount)" +
                        " from Project p" +
                        " where p.no = :projectNo", ProjectQueryDto.class)
                .setParameter("projectNo", projectNo)
                .getSingleResult();
    }

    // ProjectPositionQueryDto 리스트에서 No 값만 가져오는 메소드
    private List<Long> toProjectPositionNos(List<ProjectPositionQueryDto> projectPositionQueryDtoList) {
        return projectPositionQueryDtoList.stream().map(projectPositionQueryDto -> projectPositionQueryDto.getNo())
                .collect(Collectors.toList());
    }

    // projectPositionQueryDto 찾는 메소드
    private List<ProjectPositionQueryDto> findProjectPositionQueryDtoList(Long projectNo) {
        List<ProjectPositionQueryDto> projectPositionQueryDtoList = em.createQuery(
                        "select new com.matching.project.dto.project.ProjectPositionQueryDto(pp.no, pp.position, pp.state)" +
                                " from ProjectPosition pp" +
                                " where pp.project.no in :projectNo", ProjectPositionQueryDto.class)
                .setParameter("projectNo", projectNo)
                .getResultList();

        return projectPositionQueryDtoList;
    }

    private Map<Long, List<ProjectTechnicalStackQueryDto>> findProjectTechnicalStackQueryDtoList(List<Long> projectPositionNos) {
        List<ProjectTechnicalStackQueryDto> projectTechnicalStackQueryDtoList = em.createQuery(
                        "select new com.matching.project.dto.project.ProjectTechnicalStackQueryDto(pts.projectPosition.no, pts.no, pts.name)" +
                                " from ProjectTechnicalStack pts" +
                                " where pts.projectPosition.no in :projectPositionNos", ProjectTechnicalStackQueryDto.class)
                .setParameter("projectPositionNos", projectPositionNos)
                .getResultList();
        return projectTechnicalStackQueryDtoList.stream().collect(Collectors.groupingBy(ProjectTechnicalStackQueryDto::getProjectPositionNo));
    }
}

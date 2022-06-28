package com.matching.project.dto.project;

import com.matching.project.dto.enumerate.Position;
import com.matching.project.entity.Project;
import com.matching.project.entity.ProjectPosition;
import com.matching.project.entity.ProjectTechnicalStack;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProjectRegisterResponseDto {
    private Long no;
    private String name;
    private String profile;
    private LocalDateTime createDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean state;
    private String introduction;
    private Integer maxPeople;
    private List<ProjectPositionDto> projectPosition = new ArrayList<>();

    @Builder
    public ProjectRegisterResponseDto(Long no, String name, String profile, LocalDateTime createDate, LocalDate startDate, LocalDate endDate, boolean state, String introduction, Integer maxPeople) {
        this.no = no;
        this.name = name;
        this.profile = profile;
        this.createDate = createDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
        this.introduction = introduction;
        this.maxPeople = maxPeople;
    }

    public static ProjectRegisterResponseDto of (Project project) {
        ProjectRegisterResponseDto projectRegisterResponseDto = ProjectRegisterResponseDto.builder()
                .no(project.getNo())
                .name(project.getName())
                .profile(null)
                .createDate(project.getCreateDate())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .state(project.isState())
                .introduction(project.getIntroduction())
                .maxPeople(project.getMaxPeople())
                .build();

        List<ProjectPosition> projectPosition = project.getProjectPosition();
        for (ProjectPosition position : projectPosition) {
            ProjectPositionDto projectPositionDto = new ProjectPositionDto();
            projectPositionDto.setPosition(Position.valueOf(position.getName()));
            List<String> technicalStack = new ArrayList<>();
            for (ProjectTechnicalStack projectTechnicalStack : position.getProjectTechnicalStack()) {
                technicalStack.add(projectTechnicalStack.getName());
            }
            projectPositionDto.setTechnicalStack(technicalStack);
            projectRegisterResponseDto.getProjectPosition().add(projectPositionDto);
        }

        return projectRegisterResponseDto;
    }
}

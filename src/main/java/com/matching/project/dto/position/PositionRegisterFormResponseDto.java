package com.matching.project.dto.position;

import com.matching.project.entity.Position;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class PositionRegisterFormResponseDto {
    private Long positionNo;
    private String positionName;

    public static PositionRegisterFormResponseDto toPositionRegisterFormResponseDto (Position postion) {
        return PositionRegisterFormResponseDto.builder()
                .positionNo(postion.getNo())
                .positionName(postion.getName())
                .build();
    }
}

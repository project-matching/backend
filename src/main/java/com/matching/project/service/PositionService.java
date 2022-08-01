package com.matching.project.service;

import com.matching.project.dto.position.PositionRegisterFormResponseDto;
import com.matching.project.entity.Position;

import java.util.List;

public interface PositionService {
    List<PositionRegisterFormResponseDto> positionList();
    Position positionRegister(String positionName);
    Position positionUpdate(Long positionNo, String updatePositionName);
}

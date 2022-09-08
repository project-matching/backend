package com.matching.project.service;

import com.matching.project.dto.position.PositionRegisterFormResponseDto;
import com.matching.project.entity.Position;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PositionServiceImpl implements PositionService {
    private final PositionRepository positionRepository;

    @Override
    public List<PositionRegisterFormResponseDto> positionList() {
        List<Position> positionList = positionRepository.findAll();
        return positionList.stream()
                .map(PositionRegisterFormResponseDto::toPositionRegisterFormResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Position positionRegister(String positionName) {
        Optional<Position> optionalPosition = positionRepository.findAllByName(positionName);
        if (optionalPosition.isPresent())
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_POSITION_EXCEPTION);
        else {
            Position position = Position.builder()
                    .name(positionName)
                    .build();
            positionRepository.save(position);
            return position;
        }
    }

    @Override
    @Transactional
    public Position positionUpdate(Long positionNo, String updatePositionName) {
        List<Position> positionList = positionRepository.findAll();
        int i = 0;
        int offset = -1;
        for (; i < positionList.size(); i++) {
            // 수정할 포지션이 존재 하는 경우, 오프셋 설정
            if (positionList.get(i).getNo().equals(positionNo))
                offset = i;
            // 중복된 걸로 수정하려는 경우
            if (positionList.get(i).getName().equals(updatePositionName))
                throw new CustomException(ErrorCode.ALREADY_REGISTERED_POSITION_EXCEPTION);

        }

        // 수정할 포지션이 존재 하지 않는 경우
        if (offset == -1) {
            throw new CustomException(ErrorCode.UNREGISTERED_POSITION_EXCEPTION);
        } else {
            // 포지션 수정
            positionList.get(offset).updatePositionName(updatePositionName);
            return positionList.get(offset);
        }
    }
}

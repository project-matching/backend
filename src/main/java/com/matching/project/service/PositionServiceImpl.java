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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = Optional.ofNullable((User)auth.getPrincipal());

        List<Position> positionList = positionRepository.findAll();
        return positionList.stream()
                .map(PositionRegisterFormResponseDto::toPositionRegisterFormResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Position positionRegister(String positionName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = Optional.ofNullable((User)auth.getPrincipal());

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = Optional.ofNullable((User)auth.getPrincipal());

        Optional<Position> optionalPosition = positionRepository.findById(positionNo);
        if (optionalPosition.isEmpty())
            throw new CustomException(ErrorCode.UNREGISTERED_POSITION_EXCEPTION);
        else {
            Position position = optionalPosition.get();
            position.updatePositionName(updatePositionName);
            return position;
        }
    }
}

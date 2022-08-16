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

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        User user = null;
        if (principal instanceof User)
            user = (User)principal;
        else
            throw new CustomException(ErrorCode.GET_USER_AUTHENTICATION_EXCEPTION);
        return user;
    }

    @Override
    public List<PositionRegisterFormResponseDto> positionList() {
        User user = getAuthenticatedUser();

        List<Position> positionList = positionRepository.findAll();
        return positionList.stream()
                .map(PositionRegisterFormResponseDto::toPositionRegisterFormResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Position positionRegister(String positionName) {
        User user = getAuthenticatedUser();

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
        User user = getAuthenticatedUser();

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

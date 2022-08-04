package com.matching.project.service;

import com.matching.project.dto.enumerate.Role;
import com.matching.project.dto.position.PositionRegisterFormResponseDto;
import com.matching.project.entity.Position;
import com.matching.project.entity.User;
import com.matching.project.error.CustomException;
import com.matching.project.repository.PositionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PositionServiceImplTest {

    @Mock
    private PositionRepository positionRepository;

    @InjectMocks
    private PositionServiceImpl positionService;

    @Nested
    @DisplayName("포지션 리스트 조회")
    class PositionList {
        @DisplayName("성공")
        @Test
        void success() {
            //given
            Long userNo = 1L;
            String userName = "테스터";
            String userEmail = "leeworld9@gmail.com";
            Role userRole = Role.ROLE_ADMIN;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            List<Position> positionList = new ArrayList<>();
            positionList.add(Position.builder().no(1L).name("test1").build());
            positionList.add(Position.builder().no(2L).name("test2").build());

            given(positionRepository.findAll()).willReturn(positionList);

            //when
            List<PositionRegisterFormResponseDto> dto = positionService.positionList();

            //then
            assertThat(dto.get(0).getPositionName()).isEqualTo("test1");
            assertThat(dto.get(1).getPositionName()).isEqualTo("test2");
        }

    }

    @Nested
    @DisplayName("포지션 등록")
    class PositionRegister {
        @DisplayName("성공")
        @Test
        void success() {
            //given
            Long userNo = 1L;
            String userName = "테스터";
            String userEmail = "leeworld9@gmail.com";
            Role userRole = Role.ROLE_ADMIN;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            String positionName = "test1";
            given(positionRepository.findAllByName(positionName)).willReturn(Optional.empty());

            //when
            Position resPosition = positionService.positionRegister(positionName);

            //then
            assertThat(resPosition.getName()).isEqualTo(positionName);

            //verify
            verify(positionRepository, times(1)).save(any(Position.class));
        }

        @DisplayName("실패 : 이미 존재하는 포지션")
        @Test
        void fail() {
            //given
            Long userNo = 1L;
            String userName = "테스터";
            String userEmail = "leeworld9@gmail.com";
            Role userRole = Role.ROLE_ADMIN;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            Long positionNo = 1L;
            String positionName = "test1";
            Position position = Position.builder()
                    .no(positionNo)
                    .name(positionName)
                    .build();
            given(positionRepository.findAllByName(positionName)).willReturn(Optional.ofNullable(position));

            //when
            CustomException e = Assertions.assertThrows(CustomException.class, () -> {
                Position resPosition = positionService.positionRegister(positionName);
            });

            //then
            assertThat(e.getErrorCode().getDetail()).isEqualTo("Already Registered Position");
        }
    }

    @Nested
    @DisplayName("포지션 수정")
    class PositionUpdate {
        @DisplayName("성공")
        @Test
        void success() {
            //given
            Long userNo = 1L;
            String userName = "테스터";
            String userEmail = "leeworld9@gmail.com";
            Role userRole = Role.ROLE_ADMIN;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            Long positionNo = 1L;
            String positionName = "test1";
            Position position = Position.builder()
                    .no(positionNo)
                    .name(positionName)
                    .build();

            given(positionRepository.findById(positionNo)).willReturn(Optional.ofNullable(position));

            //when
            Position resPosition = positionService.positionUpdate(positionNo, positionName);

            //then
            assertThat(resPosition.getName()).isEqualTo(positionName);
        }

        @DisplayName("실패 : 등록되지 않는 포지션을 입력")
        @Test
        void fail() {
            //given
            Long userNo = 1L;
            String userName = "테스터";
            String userEmail = "leeworld9@gmail.com";
            Role userRole = Role.ROLE_ADMIN;

            User user = User.builder()
                    .no(userNo)
                    .name(userName)
                    .email(userEmail)
                    .permission(userRole)
                    .build();

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getEmail(), user.getAuthorities()));

            Long positionNo = 1L;
            String positionName = "test1";
            given(positionRepository.findById(positionNo)).willReturn(Optional.empty());

            //when
            CustomException e = Assertions.assertThrows(CustomException.class, () -> {
                Position resPosition = positionService.positionUpdate(positionNo, positionName);
            });

            //then
            assertThat(e.getErrorCode().getDetail()).isEqualTo("Unregistered Position");
        }
    }
}
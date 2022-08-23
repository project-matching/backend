package com.matching.project.config;

import com.matching.project.entity.User;
import com.matching.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduleTask {
    private final UserRepository userRepository;

    private final int userDeletePeriod = 6;

    // 매일 0시에 탈퇴 후 6개월이 지난 계정 정보 삭제(재가입 방지를 위해 데이터만 제거)
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void userSignOutTask1() {
        log.info("Scheduled Start");
        List<User> users = userRepository.findAll();
        if (!users.isEmpty()) {
            for (User user : users) {
                if (user.isWithdrawal()) {
                    if (user.getWithdrawalTime().plusMonths(userDeletePeriod).isBefore(LocalDateTime.now())){
                        user.deleteInfo();
                        userRepository.save(user);
                        log.info("{} => {} User Information Delete Complete", LocalDateTime.now(), user.getEmail());
                    }
                }
            }
        }
        log.info("Scheduled End");
    }
}

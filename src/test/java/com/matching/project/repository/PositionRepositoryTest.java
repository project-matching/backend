package com.matching.project.repository;

import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.entity.Position;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
@Import(QuerydslConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class PositionRepositoryTest {
    @Autowired
    PositionRepository positionRepository;

    @Test
    public void 포지션_IN_조회() {
        // given
        Position position1 = Position.builder()
                .name("testPosition1")
                .build();
        Position position2 = Position.builder()
                .name("testPosition2")
                .build();
        Position position3 = Position.builder()
                .name("testPosition3")
                .build();
        positionRepository.save(position1);
        positionRepository.save(position2);
        positionRepository.save(position3);

        // when
        List<String> names = new ArrayList<>();
        names.add("testPosition1");
        names.add("testPosition2");
        names.add("testPosition3");
        List<Position> findPositionList = positionRepository.findByNameIn(names);

        // then
        assertEquals(findPositionList.size(), 3);

        assertEquals(findPositionList.get(0).getName(), position1.getName());
        assertEquals(findPositionList.get(1).getName(), position2.getName());
        assertEquals(findPositionList.get(2).getName(), position3.getName());
    }
}
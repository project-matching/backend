package com.matching.project.repository;

import com.matching.project.config.QuerydslConfiguration;
import com.matching.project.entity.Image;
import io.swagger.models.auth.In;
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
class ImageRepositoryTest {
    @Autowired
    ImageRepository imageRepository;

    @Test
    public void 이미지_IN_조회_테스트() {
        // given
        Image image1 = Image.builder()
                .logicalName("testLogicalName1")
                .physicalName("testPhysicalName1")
                .url("testUrl1")
                .build();
        Image image2 = Image.builder()
                .logicalName("testLogicalName2")
                .physicalName("testPhysicalName2")
                .url("testUrl2")
                .build();
        Image image3 = Image.builder()
                .logicalName("testLogicalName3")
                .physicalName("testPhysicalName3")
                .url("testUrl3")
                .build();
        Image saveImage1 = imageRepository.save(image1);
        Image saveImage2 = imageRepository.save(image2);
        Image saveImage3 = imageRepository.save(image3);

        // when
        List<Long> nos = new ArrayList<>();
        nos.add(saveImage1.getNo());
        nos.add(saveImage2.getNo());

        List<Image> imageList = imageRepository.findByNoIn(nos);

        // then
        assertEquals(imageList.get(0), saveImage1);
        assertEquals(imageList.get(1), saveImage2);
    }
}
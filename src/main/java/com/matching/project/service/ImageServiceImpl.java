package com.matching.project.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.matching.project.entity.Image;
import com.matching.project.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ImageServiceImpl implements ImageService{
    private final AmazonS3Service amazonS3Service;
    private final ImageRepository imageRepository;

    @Override
    public BufferedImage imageResize(InputStream inputStream, int width, int height) throws IOException {
        BufferedImage inputImage = ImageIO.read(inputStream);

        BufferedImage outputImage =
                new BufferedImage(width, height, inputImage.getType());

        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.drawImage(inputImage, 0, 0, width, height, null);
        graphics2D.dispose();

        return outputImage;
    }

    @Override
    public void imageValidCheck(MultipartFile file) {
        final Double allowSize = 1.0; // MB 단위

        // 용량 체크 1mb 이상인지 (용량은 임시)
        double size = (double) file.getSize() / (1024.0 * 1024.0);
        log.debug("{}MB", size);
        if (size > allowSize)
            throw new IllegalArgumentException(allowSize + " MB 이상의 파일은 허용되지 않음");

        // 확장자 체크
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        if (ext == null)
            throw new IllegalArgumentException("확장자가 존재하지 않음");
        List<String> allowExt = Arrays.asList("jpg", "png", "bmp", "gif", "jpeg", "img");
        allowExt.stream()
                .filter(allow -> allow.equals(ext.toLowerCase()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("허용하지 않는 확장자"));
    }

    @Override
    public Long imageUpload(MultipartFile file, int width, int height) throws IOException{
        // 유효성 체크
        imageValidCheck(file);

        String logicalName = file.getOriginalFilename();
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String physicalName = UUID.randomUUID().toString() + "." + ext;

        log.info("{}", logicalName);
        log.info("{}", physicalName);
        log.info("{}", file.getContentType());

        // 리사이징
        log.info("이미지 리사이징 시작");
        BufferedImage resizedImage = imageResize(new BufferedInputStream(file.getInputStream()), width, height);
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ImageIO.write(resizedImage, ext, os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        log.info("이미지 리사이징 완료");

        // s3 업로드
        log.info("이미지 S3 업로드 시작");
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(os.size());
        objectMetadata.setContentType(file.getContentType());
        amazonS3Service.uploadFile(is, objectMetadata, physicalName);
        log.info("이미지 S3 업로드 완료");

        // db 저장
        Image image = Image.builder()
                .logicalName(logicalName)
                .physicalName(physicalName)
                .url(amazonS3Service.getFileUrl(physicalName))
                .build();
        return imageRepository.save(image).getNo();
    }

    @Override
    public void imageDelete(Long no) throws IOException{
        Optional<Image> image = imageRepository.findById(no);
        image.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파일 입니다."));
        log.info("이미지 S3 삭제 시작");
        amazonS3Service.deleteFile(image.get().getPhysicalName());
        log.info("이미지 S3 삭제 완료");
        imageRepository.deleteById(no);
    }







}
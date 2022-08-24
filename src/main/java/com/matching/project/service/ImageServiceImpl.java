package com.matching.project.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.matching.project.entity.Image;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import com.matching.project.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
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
    public BufferedImage imageResize(InputStream inputStream, int width, int height){
        BufferedImage inputImage = null;
        BufferedImage outputImage = null;
        try {
            inputImage = ImageIO.read(inputStream);
            outputImage = new BufferedImage(width, height, inputImage.getType());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_READ_FAIL_EXCEPTION);
        }

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
        if (size > allowSize)
            throw new CustomException(ErrorCode.SIZE_OVER_EXCEPTION);

        // 확장자 체크
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        if (ext == null)
            throw new CustomException(ErrorCode.NONEXISTENT_EXT_EXCEPTION);
        List<String> allowExt = Arrays.asList("jpg", "png", "bmp", "gif", "jpeg", "img");
        allowExt.stream()
                .filter(allow -> allow.equals(ext.toLowerCase()))
                .findAny()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_ALLOWED_EXT_EXCEPTION));
    }

    @Override
    public Long imageUpload(MultipartFile file, int width, int height){
        // 유효성 체크
        imageValidCheck(file);

        String logicalName = file.getOriginalFilename();
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String physicalName = UUID.randomUUID().toString() + "." + ext;

        // 리사이징
        log.info("Image Resize Start");
        long startTime = System.currentTimeMillis();

        BufferedImage resizedImage = null;
        try {
            resizedImage = imageResize(new BufferedInputStream(file.getInputStream()), width, height);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_CONVERSION_EXCEPTION);
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(resizedImage, ext, os);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_WRITE_FAIL_EXCEPTION);
        }
        InputStream is = new ByteArrayInputStream(os.toByteArray());

        long endTime = System.currentTimeMillis();
        log.info("Image Resize End ({}ms)", endTime - startTime);

        // s3 업로드
        log.info("Image S3 Upload Start");
        startTime = System.currentTimeMillis();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(os.size());
        objectMetadata.setContentType(file.getContentType());
        amazonS3Service.uploadFile(is, objectMetadata, physicalName);

        endTime = System.currentTimeMillis();
        log.info("Image S3 Upload End ({}ms)", endTime - startTime);

        // db 저장
        Image image = Image.builder()
                .logicalName(logicalName)
                .physicalName(physicalName)
                .url(amazonS3Service.getFileUrl(physicalName))
                .build();
        return imageRepository.save(image).getNo();
    }

    @Override
    public void imageDelete(Long no){
        Optional<Image> image = imageRepository.findById(no);
        image.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_FILE_EXCEPTION));
        log.info("Image S3 Delete Start");
        amazonS3Service.deleteFile(image.get().getPhysicalName());
        log.info("Image S3 Delete End");
        imageRepository.deleteById(no);
    }

    @Override
    public String getImageUrl(Long imageNo) {
        String imageUrl = null;
        if (imageNo != null) {
            Optional<Image> image = imageRepository.findById(imageNo);
            imageUrl = image.get().getUrl();
        }
        return imageUrl;
    }
}
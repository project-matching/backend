package com.matching.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public interface ImageService {
    BufferedImage imageResize(InputStream inputStream, int width, int height);
    void imageValidCheck(MultipartFile file);
    Long imageUpload(MultipartFile file, int width, int height) ;
    void imageDelete(Long no);
    String getImageUrl(Long imageNo);
}
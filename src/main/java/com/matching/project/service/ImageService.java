package com.matching.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public interface ImageService {
    BufferedImage imageResize(InputStream inputStream, int width, int height) throws IOException;
    void imageValidCheck(MultipartFile file);
    Long imageUpload(MultipartFile file, int width, int height) throws IOException;
    void imageDelete(Long no) throws IOException;
}
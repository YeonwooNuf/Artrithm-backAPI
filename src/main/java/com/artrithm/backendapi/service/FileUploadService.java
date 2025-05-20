package com.artrithm.backendapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    // ✅ 현재 실행 디렉토리 기준 /uploads 폴더로 설정 (맥북/윈도우 공통)
    private static final Path BASE_PATH = Paths.get(System.getProperty("user.dir"), "uploads");

    public String saveFile(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path folderPath = BASE_PATH.resolve(folder);
        Files.createDirectories(folderPath); // 폴더 없으면 생성

        Path filePath = folderPath.resolve(filename);
        file.transferTo(filePath.toFile());

        // 프론트 접근 경로로 반환 (ex: /uploads/thumbnails/...)
        return "/uploads/" + folder + "/" + filename;
    }
}

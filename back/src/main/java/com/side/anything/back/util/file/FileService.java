package com.side.anything.back.util.file;

import com.side.anything.back.exception.CustomException;
import com.side.anything.back.util.dto.response.FileInfo;
import com.side.anything.back.util.dto.response.FileResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.side.anything.back.exception.BasicExceptionEnum.*;

@Component
public class FileService {

    @Value("${spring.servlet.multipart.location}")
    private String DEFAULT_PATH;

    // 단건 파일 저장
    public FileInfo saveFile(final MultipartFile file, final FileCategory fileCategory) {

        if(file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            throw new CustomException(BAD_REQUEST, "파일이 비어있습니다");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        String storedFilename = UUID.randomUUID() + extension;
        LocalDate today = LocalDate.now();

        String fullPath = DEFAULT_PATH + "/" + fileCategory.getPath() + "/" + formatDate(today);

        File directory = new File(fullPath);

        if(!directory.exists()) {
            directory.mkdirs();
        }

        try {
            file.transferTo(new File(fullPath, storedFilename));
        }catch (IOException e) {
            throw new CustomException(INTERNAL_SERVER_ERROR, fileCategory.getName() + " 파일을 업로드할 수 없습니다");
        }

        return new FileInfo(originalFilename, storedFilename, today);
    }

    // PDF 파일 로드
    public FileResponse loadPdf(final FileCategory fileCategory, final FileInfo fileInfo) {

        Path filePath = Paths.get(DEFAULT_PATH + "/" + fileCategory.getPath() + "/" + formatDate(fileInfo.getUploadDate()))
                .resolve(fileInfo.getStoredFilename())
                .normalize();
        Resource resource = null;

        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException 에러 발생");
            throw new RuntimeException("파일 로딩 실패", e);
        }

        if(!resource.exists()) {
            throw new CustomException(NOT_FOUND, "파일을 찾을 수 없습니다");
        }

        String encodedFilename = UriUtils.encode(fileInfo.getOriginalFilename(), StandardCharsets.UTF_8);
        String contentDisposition = "inline; filename=\"" + encodedFilename + "\"";

        return new FileResponse(resource, contentDisposition);
    }

    // 단건 파일 삭제
    public void deleteFile(FileCategory fileCategory, FileInfo fileInfo) {

        File file = Paths.get(DEFAULT_PATH + "/" + fileCategory.getPath() + "/" + formatDate(fileInfo.getUploadDate()))
                .resolve(fileInfo.getStoredFilename())
                .toFile();

        if(!file.exists()) {
            throw new CustomException(NOT_FOUND, "삭제할 파일이 없습니다");
        }

        file.delete();
    }

    /* private methods */
    private String formatDate(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}

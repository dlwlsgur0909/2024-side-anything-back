package com.side.anything.back.util.dto.response;

import com.side.anything.back.util.file.BaseFileEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class FileInfo {

    private String originalFilename;
    private String storedFilename;
    private LocalDate uploadDate;

    public FileInfo(BaseFileEntity fileEntity) {
        this.originalFilename = fileEntity.getOriginalFilename();
        this.storedFilename = fileEntity.getStoredFilename();
        this.uploadDate = fileEntity.getUploadDate();
    }

}

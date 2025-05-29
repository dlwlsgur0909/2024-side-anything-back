package com.side.anything.back.util.file;

import com.side.anything.back.base.BaseEntity;
import com.side.anything.back.util.dto.response.FileInfo;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@MappedSuperclass
public abstract class BaseFileEntity extends BaseEntity {

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "stored_filename")
    private String storedFilename;

    @Column(name = "upload_date")
    private LocalDate uploadDate;

    public void setFileInfo(FileInfo fileInfo) {
        this.originalFilename = fileInfo.getOriginalFilename();
        this.storedFilename = fileInfo.getStoredFilename();
        this.uploadDate = fileInfo.getUploadDate();
    }

}

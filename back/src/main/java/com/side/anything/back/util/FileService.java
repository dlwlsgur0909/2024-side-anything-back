package com.side.anything.back.util;

import com.side.anything.back.exception.BasicExceptionEnum;
import com.side.anything.back.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.side.anything.back.exception.BasicExceptionEnum.INTERNAL_SERVER_ERROR;

@Slf4j
@Component
public class FileService {

    @Value("${spring.servlet.multipart.location}")
    private String DEFAULT_PATH;

    // 단건 파일 저장
    public String saveFile(final MultipartFile file, final String category) {

        if(file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        String storedFilename = UUID.randomUUID() + extension;

        String fullPath = DEFAULT_PATH + "/" + category + "/" + today;

        File directory = new File(fullPath);

        if(!directory.exists()) {
            directory.mkdirs();
        }

        try {
            file.transferTo(new File(fullPath, storedFilename));
        }catch (IOException e) {
            throw new CustomException(INTERNAL_SERVER_ERROR, category + " 파일을 업로드할 수 없습니다");
        }

        return storedFilename;
    }

}

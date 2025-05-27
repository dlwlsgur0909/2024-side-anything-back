package com.side.anything.back.util.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.core.io.Resource;

@Getter
@AllArgsConstructor
public class FileResponse {

    private Resource resource;
    private String contentDisposition;

}

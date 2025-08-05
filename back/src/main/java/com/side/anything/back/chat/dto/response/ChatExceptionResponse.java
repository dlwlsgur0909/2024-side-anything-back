package com.side.anything.back.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatExceptionResponse {

    private Long memberId;
    private Integer errorCode;
    private String errorMessage;

}

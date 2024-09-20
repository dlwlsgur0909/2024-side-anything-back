package com.side.anything.back.security.dto.response;

import com.side.anything.back.member.domain.Role;
import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomUserDTO {

    private String username;
    private String name;
    private String email;
    private Role role;

}

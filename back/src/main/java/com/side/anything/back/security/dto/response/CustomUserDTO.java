package com.side.anything.back.security.dto.response;

import com.side.anything.back.member.domain.Role;
import lombok.*;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomUserDTO {

    private Long id;
    private String username;
    private String name;
    private String email;
    private Role role;

    public void setId(Long id) {
        this.id = id;
    }

}

package com.side.anything.back.chat.entity;

import com.side.anything.back.base.BaseEntity;
import com.side.anything.back.companion.entity.CompanionPost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companion_post_id")
    private CompanionPost companionPost;

    @Column(name = "is_active")
    private Boolean isActive;

}

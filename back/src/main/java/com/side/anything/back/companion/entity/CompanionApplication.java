package com.side.anything.back.companion.entity;

import com.side.anything.back.base.BaseEntity;
import com.side.anything.back.companion.dto.request.CompanionApplicationSaveRequest;
import com.side.anything.back.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanionApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message")
    private String message;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CompanionApplicationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companion_post_id")
    private CompanionPost companionPost;

    public static CompanionApplication of(CompanionApplicationSaveRequest request, Member member,
                                          CompanionPost companionPost) {

        CompanionApplication companionApplication = new CompanionApplication();

        companionApplication.message = request.getMessage();
        companionApplication.status = CompanionApplicationStatus.PENDING;
        companionApplication.member = member;
        companionApplication.companionPost = companionPost;

        return companionApplication;
    }

    public void cancel() {
        this.status = CompanionApplicationStatus.CANCELLED;
    }

    public void delete() {
        this.status = CompanionApplicationStatus.DELETED;
    }

}

package com.badminton.member.dto;

import com.badminton.member.Member;
import com.badminton.member.MemberStatus;
import com.badminton.member.MembershipLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MemberResponseDTO {

    private int memberId;
    private String username;
    private String fullName;
    private String profilePicture;
    private String phone;
    private String email;
    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private MembershipLevel membershipLevel;
    private MemberStatus status;
    private String note;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // 建構子：負責將 Entity 轉換成 DTO
    public MemberResponseDTO(Member member) {
        if (member == null) return;
        this.memberId = member.getMemberId();
        this.username = member.getUsername();
        this.fullName = member.getFullName();
        this.profilePicture = member.getProfilePicture();
        this.phone = member.getPhone();
        this.email = member.getEmail();
        this.gender = member.getGender();
        this.birthday = member.getBirthday();
        this.membershipLevel = member.getMembershipLevel();
        this.status = member.getStatus();
        this.note = member.getNote();
        this.lastLoginAt = member.getLastLoginAt();
        this.createdAt = member.getCreatedAt();
        this.updatedAt = member.getUpdatedAt();
    }
}

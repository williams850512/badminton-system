package com.badminton.member.dto;

import lombok.Data;

@Data
public class GoogleLoginRequestDTO {
    private String credential; // The Google ID Token
}

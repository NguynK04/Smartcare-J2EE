package com.example.smartcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ChatPartnerResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}

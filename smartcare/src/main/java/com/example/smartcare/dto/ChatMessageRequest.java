package com.example.smartcare.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ChatMessageRequest {
    
    private Long receiverId;
    
    @NotBlank(message = "Nội dung tin nhắn không được để trống")
    private String messageContent;
}

package com.example.smartcare.controller;

import com.example.smartcare.dto.ApiResponse;
import com.example.smartcare.dto.ChatMessageRequest;
import com.example.smartcare.dto.ChatMessageResponse;
import com.example.smartcare.dto.ChatPartnerResponse;
import com.example.smartcare.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    
    /**
     * API: POST /api/v1/chat/send
     * Gửi tin nhắn từ người dùng hiện tại đến người nhận
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMessage(
            @Valid @RequestBody ChatMessageRequest request) {
        
        ChatMessageResponse message = chatService.sendMessage(request);
        
        ApiResponse<ChatMessageResponse> response = ApiResponse.<ChatMessageResponse>builder()
                .code(201)
                .message("Tin nhắn được gửi thành công!")
                .data(message)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * API: GET /api/v1/chat/history/{otherUserId}
     * Lấy lịch sử chat giữa người dùng hiện tại và người khác
     */
    @GetMapping("/history/{otherUserId}")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getChatHistory(
            @PathVariable("otherUserId") Long otherUserId) {
        
        List<ChatMessageResponse> messages = chatService.getChatHistory(otherUserId);
        
        ApiResponse<List<ChatMessageResponse>> response = ApiResponse.<List<ChatMessageResponse>>builder()
                .code(200)
                .message("Lấy lịch sử chat thành công!")
                .data(messages)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API: GET /api/v1/chat/partners
     * Lấy danh sách bệnh nhân/người dùng đã chat với CSKH
     * Chỉ cho CSKH (STAFF role)
     */
    @GetMapping("/partners")
    public ResponseEntity<ApiResponse<List<ChatPartnerResponse>>> getChatPartners() {
        try {
            List<ChatPartnerResponse> partners = chatService.getChatPartners();
            
            ApiResponse<List<ChatPartnerResponse>> response = ApiResponse.<List<ChatPartnerResponse>>builder()
                    .code(200)
                    .message("Lấy danh sách người chat thành công!")
                    .data(partners)
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Lỗi trong getChatPartners: " + e.getMessage());
            e.printStackTrace();
            
            ApiResponse<List<ChatPartnerResponse>> errorResponse = ApiResponse.<List<ChatPartnerResponse>>builder()
                    .code(400)
                    .message("Lỗi: " + e.getMessage())
                    .data(null)
                    .build();
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}

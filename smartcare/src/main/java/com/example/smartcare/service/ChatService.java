package com.example.smartcare.service;

import com.example.smartcare.dto.ChatMessageRequest;
import com.example.smartcare.dto.ChatMessageResponse;
import com.example.smartcare.dto.ChatPartnerResponse;
import com.example.smartcare.entity.ChatMessage;
import com.example.smartcare.entity.User;
import com.example.smartcare.enums.Role;
import com.example.smartcare.repository.ChatMessageRepository;
import com.example.smartcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    
    /**
     * Gửi tin nhắn từ người dùng hiện tại đến người nhận
     */
    @Transactional
    public ChatMessageResponse sendMessage(ChatMessageRequest request) {
        // 1. Get current user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người gửi!"));
        
        // 2. Find receiver
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người nhận!"));
        
        // 3. Create and save message
        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .messageContent(request.getMessageContent())
                .build();
        
        chatMessageRepository.save(message);
        
        // 4. Return response
        return ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(sender.getId())
                .senderName(sender.getFullName())
                .receiverId(receiver.getId())
                .receiverName(receiver.getFullName())
                .messageContent(message.getMessageContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
    
    /**
     * Lấy lịch sử chat giữa người dùng hiện tại và người khác
     */
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getChatHistory(Long otherUserId) {
        // 1. Get current user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));
        
        // 2. Get chat history
        List<ChatMessage> messages = chatMessageRepository.findChatHistory(currentUser.getId(), otherUserId);
        
        // 4. Convert to responses
        return messages.stream()
                .map(msg -> ChatMessageResponse.builder()
                        .id(msg.getId())
                        .senderId(msg.getSender().getId())
                        .senderName(msg.getSender().getFullName())
                        .receiverId(msg.getReceiver().getId())
                        .receiverName(msg.getReceiver().getFullName())
                        .messageContent(msg.getMessageContent())
                        .createdAt(msg.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy danh sách bệnh nhân hiện tôi (cho CSKH)
     * Chỉ CSKH (STAFF) mới được gọi hàm này
     */
    @Transactional(readOnly = true)
    public List<ChatPartnerResponse> getChatPartners() {
        // 1. Get current user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));
        
        // Verify user is CSKH/STAFF
        if (currentUser.getRole() != Role.STAFF) {
            throw new RuntimeException("Chỉ CSKH mới được truy cập danh sách chat!");
        }
        
        // 2. Get chat partners
        List<User> partners = chatMessageRepository.findChatPartners(currentUser.getId());
        
        // 3. Convert to response with latest message
        return partners.stream()
                .filter(p -> p.getRole() == Role.PATIENT)  // Only patients
                .map(p -> {
                    List<ChatMessage> recentChat = chatMessageRepository.findChatHistory(currentUser.getId(), p.getId());
                    String lastMessage = recentChat.isEmpty() ? "" : recentChat.get(0).getMessageContent();
                    
                    return ChatPartnerResponse.builder()
                            .userId(p.getId())
                            .fullName(p.getFullName())
                            .email(p.getEmail())
                            .lastMessage(lastMessage.length() > 50 ? lastMessage.substring(0, 50) + "..." : lastMessage)
                            .lastMessageTime(recentChat.isEmpty() ? null : recentChat.get(0).getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }
}

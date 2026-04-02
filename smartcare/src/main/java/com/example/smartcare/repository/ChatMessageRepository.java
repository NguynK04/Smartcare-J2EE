package com.example.smartcare.repository;

import com.example.smartcare.entity.ChatMessage;
import com.example.smartcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    /**
     * Lấy lịch sử chat giữa hai người dùng (ordered by created_at desc)
     */
    @Query("SELECT m FROM ChatMessage m WHERE " +
           "((m.sender.id = :senderId AND m.receiver.id = :receiverId) OR " +
           "(m.sender.id = :receiverId AND m.receiver.id = :senderId)) " +
           "ORDER BY m.createdAt DESC")
    List<ChatMessage> findChatHistory(@Param("senderId") Long senderId, 
                                       @Param("receiverId") Long receiverId);
    
    /**
     * Lấy tất cả người dùng đã chat với một người
     * Dùng cho CSKH: lấy danh sách bệnh nhân đã gửi tin nhắn
     */
    @Query("SELECT DISTINCT " +
           "CASE WHEN m.sender.id = :userId THEN m.receiver " +
           "ELSE m.sender END " +
           "FROM ChatMessage m " +
           "WHERE m.sender.id = :userId OR m.receiver.id = :userId " +
           "ORDER BY MAX(m.createdAt) DESC")
    List<User> findChatPartners(@Param("userId") Long userId);
}

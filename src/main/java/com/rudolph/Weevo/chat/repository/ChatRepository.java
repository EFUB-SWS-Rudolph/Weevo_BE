package com.rudolph.Weevo.chat.repository;

import com.rudolph.Weevo.chat.domain.Chat;
import com.rudolph.Weevo.chat.domain.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ChatRepository extends JpaRepository<Chat, Long> {

    Page<Chat> findByChatRoomId(Long chatRoomId, Pageable pageable);

    Chat findTopByChatRoomOrderBySentAtDesc(ChatRoom chatRoom);

    @Query("""
            SELECT COUNT(c) FROM Chat c
            WHERE c.chatRoom = :chatRoom
            AND c.isRead = false
            AND c.sender.id <> :myId
    """)
    int countUnreadMessages(@Param("chatRoom") ChatRoom chatRoom, @Param("myId") Long myId);

    @Modifying
    @Query("""
            UPDATE Chat c
            SET c.isRead = true
            WHERE c.chatRoom.id = :chatRoomId
            AND c.sender.id <> :receiverId
            AND c.isRead = false
    """)
    void markAllUnreadMessagesAsRead(@Param("chatRoomId") Long chatRoomId, @Param("receiverId") Long receiverId);
}

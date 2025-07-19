package com.rudolph.Weevo.chat.repository;

import com.rudolph.Weevo.chat.domain.Chat;
import com.rudolph.Weevo.chat.domain.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRepository extends JpaRepository<Chat, Long> {
    Page<Chat> findByChatRoomId(Long chatRoomId, Pageable pageable);

    Chat findTopByChatRoomOrderBySentAtDesc(ChatRoom chatRoom);

    int countByChatRoomAndReceiverIdAndIsReadFalse(ChatRoom chatRoom, Long receiverId);
}

package com.rudolph.Weevo.chat.repository;

import com.rudolph.Weevo.chat.domain.ChatRoom;
import com.rudolph.Weevo.chat.domain.enums.ChatCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
//    @Query("SELECT cr FROM ChatRoom cr WHERE cr.sender.id = :memberId OR cr.receiver.id = :memberId")
//    List<ChatRoom> findByMember(@Param("memberId") Long memberId);
//
//    @Query("SELECT cr FROM ChatRoom cr WHERE cr.category = :category AND (cr.sender.id = :memberId OR cr.receiver.id = :memberId)")
//    List<ChatRoom> findByCategoryAndMember(@Param("category") ChatCategory category, @Param("memberId") Long memberId);

    @Query("""
            SELECT r FROM ChatRoom r
            WHERE (r.sender.id = :memberId AND r.senderExited = false) 
                OR (r.receiver.id = :memberId AND r.receiverExited = false)
    """)
    List<ChatRoom> findActiveChatRoomsByMember(Long memberId);

    @Query("""
            SELECT r FROM ChatRoom r
            WHERE r.category = :category
            AND ((r.sender.id = :memberId AND r.senderExited = false) 
                OR (r.receiver.id = :memberId AND r.receiverExited = false))
    """)
    List<ChatRoom> findActiveChatRoomsByCategoryAndMember(@Param("category") ChatCategory category, @Param("memberId") Long memberId);
}

package com.rudolph.Weevo.chat.repository;

import com.rudolph.Weevo.chat.domain.ChatRoom;
import com.rudolph.Weevo.chat.domain.enums.ChatCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr WHERE " +
            "cr.category = 'COFFEECHAT' AND " +
            "((cr.sender.id = :memberAId AND cr.receiver.id = :memberBId) OR " +
            " (cr.sender.id = :memberBId AND cr.receiver.id = :memberAId))")
    Optional<ChatRoom> findCoffeeChatRoom(@Param("memberAId") Long memberAId,
                                          @Param("memberBId") Long memberBId);

    @Query("SELECT cr FROM ChatRoom cr WHERE " +
            "cr.sender.id = :senderId AND cr.receiver.id = :receiverId AND cr.course.id = :courseId")
    Optional<ChatRoom> findCourseChatRoom(@Param("senderId") Long senderId,
                                          @Param("receiverId") Long receiverId,
                                          @Param("courseId") Long courseId);


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

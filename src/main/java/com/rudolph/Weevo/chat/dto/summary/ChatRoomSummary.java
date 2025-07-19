package com.rudolph.Weevo.chat.dto.summary;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rudolph.Weevo.chat.domain.Chat;
import com.rudolph.Weevo.chat.domain.ChatRoom;
import com.rudolph.Weevo.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomSummary {
    private Long chatRoomId;
    private Long opponentId;
    private String opponentName;
    private String opponentProfileImageUrl;
    private String courseTitle;
    private String lastMessage;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime lastMessageSentAt;

    private int unreadCount;

    public static ChatRoomSummary from(ChatRoom chatRoom, Member member, Chat lastChat, int unreadCount){
        return ChatRoomSummary.builder()
                .chatRoomId(chatRoom.getId())
                .opponentId(member.getId())
                .opponentName(member.getName())
                .opponentProfileImageUrl(member.getImageUrl())
                .courseTitle(chatRoom.getCourse().getTitle())
                .lastMessage(lastChat != null ? lastChat.getContent() : null)
                .lastMessageSentAt(lastChat != null ? lastChat.getSentAt() : null)
                .unreadCount(unreadCount)
                .build();
    }
}

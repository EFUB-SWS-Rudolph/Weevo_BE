package com.rudolph.Weevo.chat.dto.response;

import com.rudolph.Weevo.chat.domain.Chat;
import com.rudolph.Weevo.chat.domain.ChatRoom;
import com.rudolph.Weevo.chat.dto.summary.ChatRoomSummary;
import com.rudolph.Weevo.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChatRoomListResponseDto {

    private List<ChatRoomSummary> chatRoomSummaries;

    public static ChatRoomListResponseDto from(List<ChatRoomSummary> summaries) {
        return ChatRoomListResponseDto.builder()
                .chatRoomSummaries(summaries)
                .build();
    }
}

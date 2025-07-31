package com.rudolph.Weevo.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomExistsDto implements ChatRoomStatusDto{
    private Long chatRoomId;
    private boolean exists;

    public static ChatRoomExistsDto from(Long chatRoomId, boolean status){
        return ChatRoomExistsDto.builder()
                .chatRoomId(chatRoomId)
                .exists(status)
                .build();
    }
}

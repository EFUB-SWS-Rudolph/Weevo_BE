package com.rudolph.Weevo.chat.dto.summary;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rudolph.Weevo.chat.domain.Chat;
import com.rudolph.Weevo.chat.domain.enums.ChatType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageSummary {

    private Long senderId;
    private ChatType type;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime sentAt;

    public static MessageSummary from(Chat chat) {
        return MessageSummary.builder()
                .senderId(chat.getSender() != null ? chat.getSender().getId() : null)
                .type(chat.getType())
                .content(chat.getContent())
                .sentAt(chat.getSentAt())
                .build();
    }
}

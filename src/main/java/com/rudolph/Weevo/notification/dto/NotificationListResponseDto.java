package com.rudolph.Weevo.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NotificationListResponseDto {

    private List<NotificationSummary> notifications;
    private int unreadCount;

    public static NotificationListResponseDto from(List<NotificationSummary> summaries, int unreadCount) {
        return NotificationListResponseDto.builder()
                .notifications(summaries)
                .unreadCount(unreadCount)
                .build();
    }
}

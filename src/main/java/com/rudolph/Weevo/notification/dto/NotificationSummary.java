package com.rudolph.Weevo.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rudolph.Weevo.notification.domain.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationSummary {
    private Long notificationId;
    private String title;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime createdAt;

    private boolean isRead;

    public static NotificationSummary from(Notification notification, String content) {
        return NotificationSummary.builder()
                .notificationId(notification.getId())
                .title(notification.getTitle())
                .content(content)
                .createdAt(notification.getCreatedAt())
                .isRead(notification.isRead())
                .build();
    }
}

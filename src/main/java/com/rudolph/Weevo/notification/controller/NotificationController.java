package com.rudolph.Weevo.notification.controller;

import com.rudolph.Weevo.notification.dto.NotificationListResponseDto;
import com.rudolph.Weevo.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<NotificationListResponseDto> getNotifications() {
        NotificationListResponseDto responseDto = notificationService.getNotifications();
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> readNotification(@PathVariable Long notificationId) {
        notificationService.readNotification(notificationId);
        return ResponseEntity.noContent().build();
    }
}

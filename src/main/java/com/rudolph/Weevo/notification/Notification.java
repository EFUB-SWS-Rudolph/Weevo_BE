package com.rudolph.Weevo.notification;

import com.rudolph.Weevo.notification.enums.NotiType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotiType type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_read")
    private boolean isRead;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "receiver_id", updatable = false, nullable = false)
//    private Member receiver;
}

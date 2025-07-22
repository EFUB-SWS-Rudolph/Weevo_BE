package com.rudolph.Weevo.notification.repository;

import com.rudolph.Weevo.temp.domain.Member;
import com.rudolph.Weevo.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverOrderByCreatedAtDesc(Member receiver);

    int countByReceiverAndIsReadFalse(Member receiver);
}

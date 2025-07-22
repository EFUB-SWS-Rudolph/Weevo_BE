package com.rudolph.Weevo.notification.service;

import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.global.common.code.ErrorStatus;
import com.rudolph.Weevo.global.exception.GeneralException;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.member.repository.MemberRepository;
import com.rudolph.Weevo.member.service.MemberService;
import com.rudolph.Weevo.notification.domain.Notification;
import com.rudolph.Weevo.notification.domain.enums.NotiType;
import com.rudolph.Weevo.notification.dto.NotificationListResponseDto;
import com.rudolph.Weevo.notification.dto.NotificationSummary;
import com.rudolph.Weevo.notification.repository.NotificationRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final MemberService memberService;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    // 알림 생성
    public void createNotification(NotiType notiType, Member requester, Member receiver, @Nullable String courseTitle) {
        String title = switch (notiType) {
            case CHAT -> "채팅";
            case COURSE_MATCHED, COURSE_CANCELED -> courseTitle;
        };

        Notification notification = Notification.builder()
                .title(title)
                .type(notiType)
                .isRead(false)
                .requester(requester)
                .receiver(receiver)
                .build();

        notificationRepository.save(notification);
    }

    // 알림 조회
    @Transactional(readOnly = true)
    public NotificationListResponseDto getNotifications(CustomUserPrincipal user) {
        Member receiver = memberService.findMember(user.getMemberId());

        List<Notification> notifications = notificationRepository.findByReceiverOrderByCreatedAtDesc(receiver);

        List<NotificationSummary> summaries = notifications.stream()
                .map(n -> NotificationSummary.from(n, resolveContent(n)))
                .collect(Collectors.toList());

        int unreadCount = notificationRepository.countByReceiverAndIsReadFalse(receiver);

        return NotificationListResponseDto.from(summaries, unreadCount);
    }

    private static String resolveContent(Notification notification) {
        return switch (notification.getType()) {
            case CHAT -> "새로운 메시지가 있습니다. 확인해주세요!";
            case COURSE_MATCHED -> "강의가 성사되었어요! ‘내 강의’ 탭에서 확인하세요.";
            case COURSE_CANCELED -> notification.getRequester().getNickName() + "님이 강의를 취소를 요청하셨어요. 승인 여부를 결정해주세요.";
        };
    }

    // 알림 읽음 처리
    public void readNotification(Long notificationId) {
        Notification notification = findNotification(notificationId);

        if (!notification.isRead()) {
            notification.setRead();
        }
    }

    public Notification findNotification(Long notificationId){
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ALARM_NOT_FOUND));
    }
}

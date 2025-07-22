package com.rudolph.Weevo.course.domain;

import com.rudolph.Weevo.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCourse {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_course_id")
    private Long memberCourseId;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "course_id")
    private Course course;

    /* 취소 요청 플래그 */
    @Builder.Default private boolean teacherCancelRequested  = false;
    @Builder.Default private boolean studentCancelRequested  = false;

    /** 취소 요청 처리 도메인 로직 */
    public CancelResult applyCancelRequest(boolean isTeacher) {
        if (isTeacher) {
            if (teacherCancelRequested)           return CancelResult.DUPLICATE;
            teacherCancelRequested = true;
        } else {
            if (studentCancelRequested)           return CancelResult.DUPLICATE;
            studentCancelRequested = true;
        }
        return (teacherCancelRequested && studentCancelRequested)
                ? CancelResult.BOTH
                : CancelResult.PENDING;
    }
    public enum CancelResult { PENDING, BOTH, DUPLICATE }

    /* 팩토리 */
    public static MemberCourse of(Member member, Course course) {
        return MemberCourse.builder()
                .member(member)
                .course(course)
                .build();
    }
}


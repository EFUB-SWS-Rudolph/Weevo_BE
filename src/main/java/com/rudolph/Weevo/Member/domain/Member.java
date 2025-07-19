package com.rudolph.Weevo.Member.domain;

import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.domain.MemberCourse;
import com.rudolph.Weevo.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "members")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_uuid", columnDefinition = "BINARY(16)", unique = true)
    private UUID memberId;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickName;

    @Column(name = "provider", nullable = false, length = 10) // kakao, google
    private String provider;

    @Column(name = "provider_id", nullable = false, length = 50) //kakao에서 이메일을 받을 수 없으므로 providerId를 통해 유저 구분
    private String providerId;

    @OneToMany(mappedBy = "teacher")        //강의 개설 user:course = 1:N
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "member")         //유저:강의 = N:M (수강)
    private List<MemberCourse> memberCourses = new ArrayList<>();

}

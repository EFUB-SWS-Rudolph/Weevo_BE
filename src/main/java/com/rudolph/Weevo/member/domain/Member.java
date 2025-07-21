package com.rudolph.Weevo.member.domain;

import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.domain.MemberCourse;
import com.rudolph.Weevo.global.domain.BaseEntity;
import com.rudolph.Weevo.tag.domain.Tag;
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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "student_id")
    private String studentId;

    private String department;
    private String college;
    private String email;
    private String location;

    @Column(name = "profile_image")
    private String profileImage;

    private Boolean coffeeChat;
    private Boolean donation;
    private Boolean exchange;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberInterestTag> interestTags = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberTalentTag> talentTags = new ArrayList<>();

    @OneToMany(mappedBy = "teacher")
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<MemberCourse> memberCourses = new ArrayList<>();

    public void additionalInfo(String nickName, String studentId, String college, String department, String email,
                               List<Tag> interestTagList, List<Tag> talentTagList){
        this.nickName = nickName;
        this.studentId = studentId;
        this.college = college;
        this.department = department;
        this.location = location;
        this.email = email;

        //기존 태그 초기화
        this.interestTags.clear();
        this.talentTags.clear();

        // 새로운 중간 엔티티 생성 및 할당
        interestTagList.forEach(tag ->
                this.interestTags.add(MemberInterestTag.builder()
                        .member(this)
                        .tag(tag)
                        .build())
        );
        talentTagList.forEach(tag ->
                this.talentTags.add(MemberTalentTag.builder()
                        .member(this)
                        .tag(tag)
                        .build())
        );
    }
}

package com.rudolph.Weevo.member.domain;

import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.domain.MemberCourse;
import com.rudolph.Weevo.global.domain.BaseEntity;
import com.rudolph.Weevo.member.dto.request.FixProfileRequestDto;
import com.rudolph.Weevo.tag.domain.Tag;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickName;

    @Column(name = "provider", nullable = false, length = 10)
    private String provider;

    @Column(name = "provider_id", nullable = false, length = 50)
    private String providerId;

    private String studentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    private Department department;

    private String college;
    private String email;
    private String location;

    @Column(nullable = true)
    private String profileImage;

    private Boolean coffeeChat;
    private Boolean donation;
    private Boolean exchange;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MemberInterestTag> interestTags = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MemberTalentTag> talentTags = new ArrayList<>();

    @OneToMany(mappedBy = "teacher")
    @Builder.Default
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<MemberCourse> memberCourses = new ArrayList<>();

    public void additionalInfo(
            String nickName,
            String studentId,
            String college,
            Department department,
            String location,
            List<Tag> interestTagList,
            List<Tag> talentTagList
    ) {
        this.nickName   = nickName;
        this.studentId  = studentId;
        this.college    = college;
        this.department = department;
        this.location   = location;

        //기존 태그 초기화
        this.interestTags.clear();
        this.talentTags.clear();

        // 새로운 중간 엔티티 생성 및 할당
        for (Tag tag : interestTagList) {
            this.interestTags.add(
                    MemberInterestTag.builder()
                            .member(this)
                            .tag(tag)
                            .build()
            );
        }
        for (Tag tag : talentTagList) {
            this.talentTags.add(
                    MemberTalentTag.builder()
                            .member(this)
                            .tag(tag)
                            .build()
            );
        }
    }

    public void updateProfile(FixProfileRequestDto dto, Department department) {
        if (dto.getNickname() != null) {
            this.nickName = dto.getNickname();
        }
        if (department != null) {
            this.department = department;
        }
        if (dto.getStudentId() != null) {
            this.studentId = dto.getStudentId();
        }
        if (dto.getLocation() != null) {
            this.location = dto.getLocation();
        }
        if (dto.getIsExchange() != null) {
            this.exchange = dto.getIsExchange();
        }
        if (dto.getIsCoffeeChat() != null) {
            this.coffeeChat = dto.getIsCoffeeChat();
        }
        if (dto.getIsSkillDonation() != null) {
            this.donation = dto.getIsSkillDonation();
        }

    }

    public void updateProfileImage(String url) {
        this.profileImage = url;
    }
}

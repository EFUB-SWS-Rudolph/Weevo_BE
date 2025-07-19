package com.rudolph.Weevo.Member.domain;

import com.rudolph.Weevo.global.domain.BaseEntity;
import com.rudolph.Weevo.tag.domain.Tag;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private String studentId;
    private String department;
    private String college;
    private String email;
    private String location;

    @ManyToMany
    @JoinTable(
            name = "member_interest_tags",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> interestTags = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name= "member_talent_tags",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> talentTags = new ArrayList<>();

    public void additionalInfo(String nickName, String studentId, String college, String department, String email, String location,
                              List<Tag> interestTags, List<Tag> talentTags){
        this.nickName = nickName;
        this.studentId = studentId;
        this.college = college;
        this.department = department;
        this.location = location;
        this.email = email;
        this.interestTags = interestTags;
        this.talentTags = talentTags;
    }

}

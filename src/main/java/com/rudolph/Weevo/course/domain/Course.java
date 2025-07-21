package com.rudolph.Weevo.course.domain;

import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.course.domain.enums.CourseCategory;
import com.rudolph.Weevo.course.domain.enums.CourseStatus;
import com.rudolph.Weevo.course.domain.enums.CourseType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDate recruitStartDate;     //강의 모집 시작 날짜

    @Column(nullable = false)
    private LocalDate recruitEndDate;       //강의 모집 끝 날짜

    @Enumerated(EnumType.STRING)
    private CourseType courseType;      // 강의 타입 ex)'재능 기부'

    @Enumerated(EnumType.STRING)
    private CourseCategory courseCategory;      //강의 카테고리 ex)'미술'

    @Enumerated(EnumType.STRING)
    private CourseStatus courseStatus;      //강의 진행 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="teacher_id")
    private Member teacher;

    @OneToMany(mappedBy = "course")     // 강의 사진들 1:N
    private List<CourseImage> courseImages = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<MemberCourse> memberCourses = new ArrayList<>();
}

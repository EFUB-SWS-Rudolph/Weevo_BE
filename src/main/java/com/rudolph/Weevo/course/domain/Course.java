package com.rudolph.Weevo.course.domain;

import com.rudolph.Weevo.course.domain.enums.CourseCity;
import com.rudolph.Weevo.global.domain.BaseEntity;
import com.rudolph.Weevo.member.domain.Member;
import com.rudolph.Weevo.course.domain.enums.CourseCategory;
import com.rudolph.Weevo.course.domain.enums.CourseType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "course_start_date", nullable = false)
    private LocalDate courseStartDate;

    @Column(name = "course_end_date", nullable = false)
    private LocalDate courseEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseType courseType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseCategory courseCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseCity courseCity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Member teacher;

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseImage> courseImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberCourse> memberCourses = new ArrayList<>();
}

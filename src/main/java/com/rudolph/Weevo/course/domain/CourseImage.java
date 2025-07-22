package com.rudolph.Weevo.course.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_img_id")
    private Long courseImgId;

    private String courseImgUrl;

    private boolean isThumbnail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "course_id")
    private Course course;
}

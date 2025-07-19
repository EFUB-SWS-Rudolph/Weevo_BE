package com.rudolph.Weevo.course.domain;

import com.rudolph.Weevo.course.domain.enums.CourseType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private CourseType courseType;

    private LocalDate recruitStartDate;
    private LocalDate recruitEndDate;
}

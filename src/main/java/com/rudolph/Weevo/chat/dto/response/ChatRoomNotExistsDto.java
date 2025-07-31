package com.rudolph.Weevo.chat.dto.response;

import com.rudolph.Weevo.chat.dto.summary.CourseSummary;
import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.domain.CourseImage;
import com.rudolph.Weevo.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomNotExistsDto implements ChatRoomStatusDto{
    private Long chatRoomId;
    private boolean exists;
    private String opponentName;
    private String courseTitle;
    private String courseImageUrl;
    private String recruitStartDate;
    private String recruitEndDate;

    public static ChatRoomNotExistsDto from(Long chatRoomId, boolean status, Member opponent, Course course){
        String thumbnailImageUrl = null;

        if (course != null && course.getCourseImages() != null) {
            thumbnailImageUrl = course.getCourseImages().stream()
                    .filter(CourseImage::isThumbnail)
                    .map(CourseImage::getCourseImgUrl)
                    .findFirst()
                    .orElse(null);
        }

        return ChatRoomNotExistsDto.builder()
                .chatRoomId(chatRoomId)
                .exists(status)
                .opponentName(opponent.getNickName())
                .courseTitle(course != null ? course.getTitle() : null)
                .courseImageUrl(thumbnailImageUrl)
                .recruitStartDate(course != null ? course.getCourseStartDate().toString() : null)
                .recruitEndDate(course != null ? course.getCourseEndDate().toString() : null)
                .build();
    }
}

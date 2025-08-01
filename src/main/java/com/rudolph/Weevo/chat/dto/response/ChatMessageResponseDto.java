package com.rudolph.Weevo.chat.dto.response;

import com.rudolph.Weevo.chat.domain.Chat;
import com.rudolph.Weevo.chat.dto.summary.CourseSummary;
import com.rudolph.Weevo.chat.dto.summary.MessageSummary;
import com.rudolph.Weevo.chat.dto.summary.OpponentSummary;
import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.course.domain.CourseImage;
import com.rudolph.Weevo.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ChatMessageResponseDto {

    private Long chatRoomId;
    private OpponentSummary opponent;
    private CourseSummary course;
    private List<MessageSummary> messages;

    public static ChatMessageResponseDto from(Long chatRoomId,
                                              Member opponent,
                                              Course course,
                                              List<Chat> chatMessages) {

        String thumbnailImageUrl = course.getCourseImages().stream()
                .filter(CourseImage::isThumbnail)
                .map(CourseImage::getCourseImgUrl)
                .findFirst()
                .orElse(null);

        return ChatMessageResponseDto.builder()
                .chatRoomId(chatRoomId)
                .opponent(OpponentSummary.from(opponent))
                .course(CourseSummary.from(course, thumbnailImageUrl))
                .messages(chatMessages.stream().map(MessageSummary::from).collect(Collectors.toList()))
                .build();
    }
}

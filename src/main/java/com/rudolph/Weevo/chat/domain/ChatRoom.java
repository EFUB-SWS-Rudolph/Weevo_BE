package com.rudolph.Weevo.chat.domain;

import com.rudolph.Weevo.chat.domain.enums.ChatCategory;
import com.rudolph.Weevo.course.domain.Course;
import com.rudolph.Weevo.temp.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChatCategory category;

    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chats = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", updatable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", updatable = false, nullable = false)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", updatable = false, nullable = false)
    private Member receiver;

    @Builder.Default
    @Column(name = "sender_exited")
    private boolean senderExited = false;

    @Builder.Default
    @Column(name = "receiver_exited")
    private boolean receiverExited = false;

    public void setSenderExited() {
        this.senderExited = true;
    }

    public void setReceiverExited() {
        this.receiverExited = true;
    }
}

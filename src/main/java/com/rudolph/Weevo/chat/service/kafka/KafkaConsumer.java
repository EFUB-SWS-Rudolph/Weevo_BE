package com.rudolph.Weevo.chat.service.kafka;

import com.rudolph.Weevo.chat.domain.Chat;
import com.rudolph.Weevo.chat.dto.request.ChatMessage;
import com.rudolph.Weevo.chat.dto.summary.MessageSummary;
import com.rudolph.Weevo.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final SimpMessagingTemplate template;
    private final ChatService chatService;

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics="${message.topic.name}")
    public void listenChat(ChatMessage chatMessage){
        log.info("Received message: {}", chatMessage.getContent());
        try {
            Chat chat = chatService.findLatestByChatRoomAndSender(chatMessage.getChatRoomId(), chatMessage.getSenderId(), chatMessage.getContent());
            MessageSummary summary = MessageSummary.from(chat);
            template.convertAndSend("/sub/chat/" + chatMessage.getChatRoomId(), summary);
            log.info("Message sent to WebSocket via STOMP");
        } catch (Exception e) {
            log.error("Error sending message to WebSocket", e);
        }
    }
}

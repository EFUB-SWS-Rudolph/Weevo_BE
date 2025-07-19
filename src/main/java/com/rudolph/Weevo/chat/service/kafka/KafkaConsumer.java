package com.rudolph.Weevo.chat.service.kafka;

import com.rudolph.Weevo.chat.dto.request.ChatMessage;
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

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics="${message.topic.name}")
    public void listenChat(ChatMessage chatMessage){
        log.info("Received message: {}", chatMessage.getContent());
        try {
            template.convertAndSend("/topic/chat-room/" + chatMessage.getChatRoomId(), chatMessage);
            log.info("Message sent to WebSocket via STOMP");
        } catch (Exception e) {
            log.error("Error sending message to WebSocket", e);
        }
    }
}

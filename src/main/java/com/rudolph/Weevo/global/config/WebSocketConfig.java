package com.rudolph.Weevo.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${rabbitmq.host}")
    private String rabbitHost;

    @Value("${rabbitmq.port}")
    private int rabbitPort;

    @Value("${rabbitmq.username}")
    private String rabbitUsername;

    @Value("${rabbitmq.password}")
    private String rabbitPassword;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메세지 브로커 설정
        config.enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/queue")
                .setRelayHost(rabbitHost)
                .setRelayPort(rabbitPort)
                .setSystemLogin(rabbitUsername)
                .setSystemPasscode(rabbitPassword)
                .setClientLogin(rabbitUsername)
                .setClientPasscode(rabbitPassword);

        // 메세지 발행 url
        config.setPathMatcher(new AntPathMatcher("."));
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // socketJs 클라이언트가 WebSocket 핸드셰이크를 하기 위해 연결할 endpoint
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*");
    }
}
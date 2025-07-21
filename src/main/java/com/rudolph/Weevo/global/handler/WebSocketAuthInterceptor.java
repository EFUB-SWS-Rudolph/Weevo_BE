package com.rudolph.Weevo.global.handler;

import com.rudolph.Weevo.auth.security.CustomUserPrincipal;
import com.rudolph.Weevo.global.exception.UnauthorizedException;
import com.rudolph.Weevo.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            try {
                String jwtToken = accessor.getFirstNativeHeader("Authorization");

                if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                    jwtToken = jwtToken.substring(7);
                    String memberId = jwtUtil.getMemberIdFromToken(jwtToken);
                    UUID uuid = UUID.fromString(memberId);
                    Collection<? extends GrantedAuthority> authorities = Collections.emptyList();

                    CustomUserPrincipal principal = new CustomUserPrincipal(uuid, authorities);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);

                    accessor.setUser(authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (UnauthorizedException e) {
                throw new MessagingException("STOMP 연결 실패: " + e.getMessage(), e);
            }
        }
        return message;
    }
}


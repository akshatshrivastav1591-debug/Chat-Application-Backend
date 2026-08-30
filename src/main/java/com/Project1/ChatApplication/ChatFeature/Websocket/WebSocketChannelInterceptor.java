package com.Project1.ChatApplication.ChatFeature.Websocket;

import com.Project1.ChatApplication.Security.Jwt.JwtFilterServiceClass;
import com.Project1.ChatApplication.Security.SecurityService.UserSecurityService;
import com.Project1.ChatApplication.Security.UserIdGeneration.USerIdUtilMethods;
import com.Project1.ChatApplication.Security.UserPrinciple.UserPrinciple;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {
    private final JwtFilterServiceClass jwtService;
    private final USerIdUtilMethods userIdObj;
    private final UserSecurityService securityService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String userContactNo = userIdObj.getUserMobileNo(jwtService.extractUserId(token));
                UserPrinciple userPrinciple = new UserPrinciple(securityService.webSocketAuthenticationUtility(userContactNo));

                if (jwtService.validateToken(token, userPrinciple)) {
                    accessor.getSessionAttributes().put("userId", jwtService.extractUserId(token));
                    return message;
                }
            }
            return null; // reject connection
        }
        return message; // let all other frame types through unmodified
    }
}

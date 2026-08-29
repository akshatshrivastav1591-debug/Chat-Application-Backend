package com.Project1.ChatApplication.ChatFeature.Websocket;

import com.Project1.ChatApplication.Security.Jwt.JwtFilterServiceClass;
import com.Project1.ChatApplication.Security.SecurityService.UserSecurityService;
import com.Project1.ChatApplication.Security.UserIdGeneration.USerIdUtilMethods;
import com.Project1.ChatApplication.Security.UserPojo.UserSecurityPojoClass;
import com.Project1.ChatApplication.Security.UserPrinciple.UserPrinciple;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {
    @Autowired
    private final JwtFilterServiceClass jwtService; // your existing JWT service
    @Autowired
    USerIdUtilMethods userIdObj;
     @Autowired
    UserSecurityService securityService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            // get token from cookie (since you're using credentials:include)
            Cookie[] cookies = servletRequest.getServletRequest().getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("jwt")) {
                        // replace with your cookie name
                        String token = cookie.getValue();
                        String userContactNo = userIdObj.getUserMobileNo(jwtService.extractUserId(token));
                        UserPrinciple userPrinciple=new UserPrinciple(securityService.webSocketAuthenticationUtility(userContactNo));
                        if (jwtService.validateToken(token, userPrinciple)) {
                            // store userId in socket session for later use

                            attributes.put("userId", jwtService.extractUserId(token));
                            return true; // allow connection
                        }
                    }
                }
            }
        }
        return false; // reject connection
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
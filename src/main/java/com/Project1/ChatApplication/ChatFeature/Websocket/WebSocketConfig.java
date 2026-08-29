package com.Project1.ChatApplication.ChatFeature.Websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.w3c.dom.ls.LSOutput;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat") // it is a url through which user connect to web socket
                .setAllowedOriginPatterns("*") // it allowed the requests from the above mentioned url
                .addInterceptors(webSocketAuthInterceptor)
                .withSockJS(); //if web socket fails it use http pooling to prevent the app crashing

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry Config) {
        Config.enableSimpleBroker("/topic"); // it handle the messasging betweeen clinet and server
        // basically in below given demo method we send the return value of the method to all subsicribed user
        //with the help of @SendTo("/topic/messages") anotation
        Config.setApplicationDestinationPrefixes("/app"); //it handle the messages between client and server
        //by using messagemapping we can define methods to handle the meassage operation like we have
        //done in the below method

    }
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {

    }

}

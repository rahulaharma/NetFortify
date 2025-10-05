package com.spring.NetFortify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker //enables websocket message handling using STOMP
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){ //behind the scenes spring will create the object of WebSocketConfig and will call this function with that object passing an object of messagebroker  registery
        config.enableSimpleBroker("/topic"); // if the request from client to server is handled by @Controller then the response from server to the client will be handled by message broker. The server will send any response to /topic its the job of message broker to get that response and give it to all the subscribed clients;
        config.setApplicationDestinationPrefixes("/app"); // if a request comes from the client with /app/* then route that request to a java controller not message broker and use @MessgeMapping to handle that request as it doesn't based on http protocol
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS(); // registers a websocket endpoint that client will connect to
        //this line used to make a websocket or stomp based connection . The HTTP request will be modified to websocket protocol
// with sockjs provides fallback for websockets .If a browser is not compatible with websockets then SockJS() will use the pure HTTP based techniques like polling or long polling
    }
}

//modern appliactions use both HTTP and Websocket simountionsly

// in this application there is no need for config.setApplicationDestinationPrefixes("/app"); it's just for scalibility
//an HTTP request will not close the existing WebSocket connection. They are designed to operate as separate and parallel communication channels between the client and server.
package nl.devoorkant.sbdr.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@PropertySource("classpath:sbdr.properties")
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/mbrtws");
        config.setApplicationDestinationPrefixes("/app");
    }

    //@Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/mbrtws-websocket").withSockJS();
    }

}

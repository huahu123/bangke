package com.neuq.info.websocket;

/**
 * @AUTHOR lindexiang
 * @DATE 下午9:58
 */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(WebSocketPushHandler(), "/webSocketServer.action").addInterceptors(new MyWebSocketInterceptor()).setAllowedOrigins("*");
        registry.addHandler(WebSocketPushHandler(), "/sockjs/webSocketServer.action")
                .addInterceptors(new MyWebSocketInterceptor()).withSockJS();
    }

    @Bean
    public WebSocketHandler WebSocketPushHandler() {
        return new WebSocketPushHandler();
    }

}

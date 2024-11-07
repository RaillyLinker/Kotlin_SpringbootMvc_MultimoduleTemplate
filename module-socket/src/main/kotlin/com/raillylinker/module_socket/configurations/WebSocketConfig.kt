package com.raillylinker.module_socket.configurations

import com.raillylinker.module_socket.web_socket_handlers.TestWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean

// [WebSocket 연결 설정]
/*
     순수 WebSocket 연결 설정.
     WebSocketStompConfig 와 같은 주소 공간을 공유하므로 주의.
     만약 특별한 일이 없다면 이것보단 session 관리, 외부 메세지 브로커를 이용 가능한 WebSocketStompConfig 를 사용하는 것이 좋음.
     web_socket_handler 화 함께 사용
 */
@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        // (Websocket 연결 url 과 핸들러 연결)
        /*
             addHandler 에서 paths 를 /ws/test 로 설정했다면,
             JavaScript 에서는,
             var websocket = new SockJS('http://localhost:8080/ws/test');
             이렇게 연결하면 됩니다.
         */
        registry
            .addHandler(
                // 아래 주소로 접속하면 실행될 핸들러
                TestWebSocketHandler(),
                // "ws://localhost:8080/ws/test" 로 접속
                "/ws/test"
            )
            // webSocket 연결 CORS 는 WebConfig 가 아닌 여기서 설정
            .setAllowedOriginPatterns("*")
            .withSockJS() // 이것을 사용하면 "ws://localhost:8080/ws/test/websocket" 로 접속
            .setClientLibraryUrl("https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.2/sockjs.js")
    }

    // websocket 관련 설정
    @Bean
    fun createWebSocketContainer(): ServletServerContainerFactoryBean {
        val container = ServletServerContainerFactoryBean()
        container.setMaxTextMessageBufferSize(8192)
        container.setMaxBinaryMessageBufferSize(8192)
        return container
    }
}
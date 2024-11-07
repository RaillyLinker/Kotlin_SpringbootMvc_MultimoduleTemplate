package com.raillylinker.module_socket.web_socket_handlers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// [1대1 웹 소켓 테스팅 핸들러]
// configurations/WebSocketConfig 에서 핸들러 설정시 사용됩니다.
// 텍스트 데이터 양방향 연결
class TestWebSocketHandler : TextWebSocketHandler() {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)

    // (현재 웹 소켓에 연결된 세션 리스트)
    private val webSocketSessionHashMap: ConcurrentHashMap<String, WebSocketSession> = ConcurrentHashMap()

    // (스레드 풀)
    private val executorService: ExecutorService = Executors.newCachedThreadPool()


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (Client 연결 콜백)
    override fun afterConnectionEstablished(
        // 연결된 클라이언트 세션
        webSocketSession: WebSocketSession
    ) {
        // 웹 소켓 세션을 추가
        webSocketSessionHashMap[webSocketSession.id] = webSocketSession
    }

    // (Client 해제 콜백)
    @Throws(Exception::class)
    override fun afterConnectionClosed(
        // 연결 해제된 클라이언트 세션
        webSocketSession: WebSocketSession,
        status: CloseStatus
    ) {
        // 세션을 리스트에서 제거
        webSocketSessionHashMap.remove(webSocketSession.id)
    }

    // (텍스트 메세지 수신 콜백)
    @Throws(Exception::class)
    override fun handleTextMessage(
        // 메세지를 보낸 클라이언트 세션
        webSocketSession: WebSocketSession,
        // 수신된 메세지
        message: TextMessage
    ) {
        // 보내온 String 메세지를 객체로 해석
        val messagePayloadVo = Gson().fromJson<MessagePayloadVo>(
            message.payload, // 해석하려는 json 형식의 String
            object : TypeToken<MessagePayloadVo>() {}.type // 파싱할 객체 타입
        )

        classLogger.info("messagePayloadVo : $messagePayloadVo")

        // 메세지 수신 후 몇초 후 서버 사이드에서 메세지 전송
        executorService.execute {
            // 범위 랜덤 밀리초 대기
            val randomMs = (0L..2000L).random()
            Thread.sleep(randomMs)

            // 메세지를 보낸 클라이언트에게 메세지 전송
            webSocketSession.sendMessage(
                TextMessage(
                    // 객체를 Json String 으로 해석
                    Gson().toJson(
                        MessagePayloadVo(
                            "Server",
                            "$randomMs 밀리초 대기 후 전송함"
                        )
                    )
                )
            )
        }
    }


    // ---------------------------------------------------------------------------------------------
    // <비공개 메소드 공간>


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    // (메세지 Vo)
    /*
         고도화시에는 아래 VO 에 더 많은 정보를 저장하여 이를 이용하여 기능을 구현하세요.
         일반적으로 양방향 연결이 필요한 기능인 채팅에 관련하여,
         필요한 기능들에 필요한 형식을 미리 만들어서 제공해주는 프로토콜인 STOMP 를 사용할 수도 있습니다.
     */
    data class MessagePayloadVo(
        val sender: String, // 송신자 (실제로는 JWT 로 보안 처리를 할 것)
        val message: String
    )

}
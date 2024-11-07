package com.raillylinker.module_socket.services

import com.raillylinker.module_socket.controllers.WebSocketStompController

interface WebSocketStompService {
    // (/test 로 받아서 /topic 토픽을 구독중인 모든 클라이언트에 메시지 전달)
    fun sendToTopicTest(inputVo: WebSocketStompController.SendToTopicTestInputVo): WebSocketStompController.TopicVo
}
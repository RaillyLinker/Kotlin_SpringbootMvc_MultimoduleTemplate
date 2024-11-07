package com.raillylinker.module_common.classes

import okhttp3.*
import okio.BufferedSource
import java.io.IOException
import java.util.concurrent.TimeUnit

// [SseClient 클래스]
class SseClient(
    private val requestUrl: String
) {
    // <멤버 변수 공간>
    private var originalRequest: Request? = null
    private var callObject: Call? = null
    private var lastEventId: String? = null
    private val dataStringBuilder = StringBuilder()
    private var eventName = "message"
    private var bufferedSource: BufferedSource? = null


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (SSE 구독 연결)
    fun connect(
        readTimeOutMs: Long, // 수신 타임아웃 밀리초
        listenerCallback: ListenerCallback // 리스너 콜백
    ) {
        // 비동기 실행
        // request 객체 생성
        originalRequest = Request.Builder().url(requestUrl).build()

        // request 객체 첫 설정 시점을 멤버에게 패스
        listenerCallback.onConnectRequestFirstTime(this, originalRequest!!)

        val okHttpClient = OkHttpClient
            .Builder()
            .readTimeout(readTimeOutMs, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .build()

        callObject = okHttpClient.newCall(
            // SSE 연결에 필요한 request 객체 설정
            originalRequest!!.newBuilder()
                .header("Accept-Encoding", "")
                .header("Accept", "text/event-stream")
                .header("Cache-Control", "no-cache").build()
        ).apply {
            // SSE 구독 요청하기
            this.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // 구독 요청 실패시
                    // 재요청 반복
                    if (!retry(readTimeOutMs, okHttpClient, listenerCallback, e, null)) {
                        listenerCallback.onDisconnected(this@SseClient)
                        disconnect()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        // 구독 요청 성공시
                        response.body.use { body ->
                            bufferedSource = body!!.source()
                            bufferedSource?.timeout()?.timeout(readTimeOutMs, TimeUnit.MILLISECONDS)
                            listenerCallback.onConnect(this@SseClient, response)
                            while (true) {
                                if (callObject == null ||
                                    callObject!!.isCanceled() ||
                                    !read(
                                        readTimeOutMs,
                                        okHttpClient,
                                        listenerCallback
                                    )
                                ) {
                                    break
                                }
                            }
                        }
                    } else {
                        // 구독 요청 실패시
                        if (!retry(
                                readTimeOutMs,
                                okHttpClient,
                                listenerCallback,
                                IOException(response.message),
                                response
                            )
                        ) {
                            listenerCallback.onDisconnected(this@SseClient)
                            disconnect()
                        }
                    }
                }
            })
        }
    }

    // (SSE 구독 해제)
    fun disconnect() {
        if (callObject != null && !callObject!!.isCanceled()) {
            callObject!!.cancel()
            callObject = null
        }
        lastEventId = null
        bufferedSource = null
        dataStringBuilder.clear()
        eventName = "message"
        originalRequest = null
    }


    // ---------------------------------------------------------------------------------------------
    // <비공개 메소드 공간>
    private fun retry(
        readTimeoutMsMbr: Long,
        clientMbr: OkHttpClient,
        listenerCallback: ListenerCallback,
        throwable: Throwable,
        response: Response?
    ): Boolean {
        if (!Thread.currentThread().isInterrupted &&
            !callObject!!.isCanceled() &&
            listenerCallback.onPreRetry(this, originalRequest!!, throwable, response)
        ) {
            val requestBuilder: Request.Builder = originalRequest!!.newBuilder()
                .header("Accept-Encoding", "")
                .header("Accept", "text/event-stream")
                .header("Cache-Control", "no-cache")
            if (lastEventId != null) {
                requestBuilder.header("Last-Event-Id", lastEventId!!)
            }
            callObject = clientMbr.newCall(requestBuilder.build())
            try {
                Thread.sleep(3000L)
            } catch (ignored: InterruptedException) {
                return false
            }
            if (!Thread.currentThread().isInterrupted && !callObject!!.isCanceled()) {
                callObject!!.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        if (!retry(readTimeoutMsMbr, clientMbr, listenerCallback, e, null)) {
                            listenerCallback.onDisconnected(this@SseClient)
                            disconnect()
                        }
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            response.body.use { body ->
                                bufferedSource = body!!.source()
                                bufferedSource?.timeout()?.timeout(readTimeoutMsMbr, TimeUnit.MILLISECONDS)
                                listenerCallback.onConnect(this@SseClient, response)
                                while (true) {
                                    if (callObject == null || callObject!!.isCanceled() || !read(
                                            readTimeoutMsMbr,
                                            clientMbr,
                                            listenerCallback
                                        )
                                    ) {
                                        break
                                    }
                                }
                            }
                        } else {
                            if (!retry(
                                    readTimeoutMsMbr,
                                    clientMbr,
                                    listenerCallback,
                                    IOException(response.message),
                                    response
                                )
                            ) {
                                listenerCallback.onDisconnected(this@SseClient)
                                disconnect()
                            }
                        }
                    }
                })
                return true
            }
        }
        return false
    }

    private fun read(readTimeoutMsMbr: Long, clientMbr: OkHttpClient, listenerCallback: ListenerCallback): Boolean {
        try {
            val line = bufferedSource!!.readUtf8LineStrict()
            if (line.isEmpty()) {
                if (dataStringBuilder.isNotEmpty()) {
                    var dataString = dataStringBuilder.toString()
                    if (dataString.endsWith("\n")) {
                        dataString = dataString.substring(0, dataString.length - 1)
                    }
                    listenerCallback.onMessageReceive(this@SseClient, lastEventId, eventName, dataString)
                    dataStringBuilder.setLength(0)
                    eventName = "message"
                }
            } else {
                val colonIndex = line.indexOf(':')
                if (colonIndex == 0) {
                    listenerCallback.onCommentReceive(this@SseClient, line.substring(1).trim { it <= ' ' })
                } else if (colonIndex != -1) {
                    val field = line.substring(0, colonIndex)
                    var value = ""
                    var valueIndex = colonIndex + 1
                    if (valueIndex < line.length) {
                        if (line[valueIndex] == ' ') {
                            valueIndex++
                        }
                        value = line.substring(valueIndex)
                    }
                    when (field) {
                        "data" -> {
                            dataStringBuilder.append(value).append('\n')
                        }

                        "id" -> {
                            lastEventId = value
                        }

                        "event" -> {
                            eventName = value
                        }
                    }
                } else {
                    when (line) {
                        "data" -> {
                            dataStringBuilder.append("").append('\n')
                        }

                        "id" -> {
                            lastEventId = ""
                        }

                        "event" -> {
                            eventName = ""
                        }
                    }
                }
            }
        } catch (e: IOException) { // SSE 타임아웃 등
            if (!retry(readTimeoutMsMbr, clientMbr, listenerCallback, e, null)) {
                listenerCallback.onDisconnected(this@SseClient)
                disconnect()
            }
            return false
        }
        return true
    }


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    interface ListenerCallback {
        // 처음 SSE 구독시 request 객체가 처음 생성 되었을 때(connectAsync 메소드 실행시) 한번 실행
        fun onConnectRequestFirstTime(sse: SseClient, originalRequest: Request)

        // SSE 연결, 재연결 되었을 때마다
        fun onConnect(sse: SseClient, response: Response)

        // SSE 메세지 수신시
        fun onMessageReceive(sse: SseClient, eventId: String?, event: String, message: String)

        // SSE Comment 수신시
        fun onCommentReceive(sse: SseClient, comment: String)

        // 재 연결을 신청할 때마다 실행 (반환 값으로 재연결 여부를 반환)
        fun onPreRetry(
            sse: SseClient,
            originalRequest: Request,
            throwable: Throwable,
            response: Response?
        ): Boolean

        // SSE 구독 연결이 끊겼을 때
        fun onDisconnected(sse: SseClient)
    }
}
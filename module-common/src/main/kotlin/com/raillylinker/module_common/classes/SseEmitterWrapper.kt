package com.raillylinker.module_common.classes

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

// [SseEmitter 래핑 클래스]
class SseEmitterWrapper {
    // (SSE Emitter 의 만료시간 Milli Sec)
    private val sseEmitterExpireTimeMs: Long = 1000L * 10L

    // (SSE Emitter 의 만료시간 이후 생존시간 Milli Sec)
    private val sseEmitterSurviveTimeMs: Long = 5000L

    /*
        (SSE Emitter 를 고유값과 함께 모아둔 맵)
         map key = EmitterId
         map value = Pair(createTimeMillis, SseEmitter)
     */
    private val emitterMap: ConcurrentHashMap<String, Pair<Long, SseEmitter>> = ConcurrentHashMap()

    /*
        (발행 이벤트 맵)
         map key = EmitterId
         map value = ArrayList(Pair(createTimeMillis, EventBuilder))

         key 는 emitterMap 과 동일한 값을 사용.
         value 의 ArrayList 는 이벤트 발행시간과 SseEventBuilder 객체의 Pair 로 이루어짐
     */
    private val eventHistoryMap: ConcurrentHashMap<String, ArrayList<Pair<Long, SseEmitter.SseEventBuilder>>> =
        ConcurrentHashMap()

    // (발행 시퀀스)
    // emitter 고유성 보장을 위한 값으로 사용되며, 유한한 값이지만, 현재 날짜와 같이 사용됩니다.
    private var emitterIdSequence: Long = 0L

    // (스레드 풀)
    private val executorService: ExecutorService = Executors.newCachedThreadPool()


    // (SSE Emitter 객체 발행)
    // !! 주의 : 함수 사용시 꼭 이 클래스 멤버변수인 emitterMapSemaphore, emitterEventMapSemaphore 로 감쌀것. !!
    fun getSseEmitter(
        // 멤버고유번호(비회원은 null)
        memberUid: Long?,
        // 마지막으로 클라이언트가 수신했던 이벤트 아이디 ({EmitterId}/{발송시간})
        lastSseEventId: String?
    ): SseEmitter {
        val lastSseEventIdSplit: List<String>?

        // SSE Emitter ID 결정
        val sseEmitterId = if (lastSseEventId == null) {
            lastSseEventIdSplit = null
            // 첫 발행시 Emitter ID 생성
            // sseEmitter 아이디 (멤버고유번호(비회원은 "null")_객체 아이디 발행일_발행총개수)
            "${memberUid}_${System.currentTimeMillis()}_${emitterIdSequence++}"
        } else {
            // 기존 Emitter ID 재활용
            lastSseEventIdSplit = lastSseEventId.split("/")
            lastSseEventIdSplit[0] // 지난번 발행된 emitter id
        }

        // sseEmitter 생성
        val sseEmitter = SseEmitter(sseEmitterExpireTimeMs)

        // 생성된 sseEmitter 및 생성시간 저장
        emitterMap[sseEmitterId] = Pair(System.currentTimeMillis(), sseEmitter)

        // SSE Emitter 콜백 설정
        sseEmitter.onTimeout { // 타임아웃 시 실행
            // 이후 바로 onCompletion 이 실행되고,다시 lastSseEventId 를 넣은 클라이언트 요청으로 현 API 가 재실행됨
            sseEmitter.complete()
        }

        sseEmitter.onError { _ -> // 에러 발생시 실행.
            // 대표적으로 클라이언트가 연결을 끊었을 때 실행됨.
            // 이후 바로 onCompletion 이 실행되고, 함수는 재실행되지 않음.
        }

        sseEmitter.onCompletion { // sseEmitter 가 종료되었을 때 공통적, 최종적으로 실행
        }

        // 503 에러를 방지하기 위해, 처음 이미터 생성시엔 빈 메세지라도 발송해야함
        try {
            sseEmitter.send(
                SseEmitter
                    .event()
                    .name("system")
                    .data("SSE Connected!")
            )
        } catch (_: Exception) {
        }

        if (lastSseEventIdSplit != null) {
            // 마지막으로 이벤트를 수신한 시간
            val lastEventTimeMillis = lastSseEventIdSplit[1].toLong()

            // lastSseEventId 로 식별하여 다음에 발송해야할 이벤트들을 전송하기
            // 쌓인 event 처리
            if (eventHistoryMap.containsKey(sseEmitterId)) {
                // 지난 이벤트 리스트 가져오기
                val eventHistoryList = eventHistoryMap[sseEmitterId]!!
                // 지난 이미터 정보를 이벤트 맵에서 제거
                eventHistoryMap.remove(sseEmitterId)

                for (eventHistory in eventHistoryList) {
                    val pastEventTimeMillis = eventHistory.first
                    val pastEvent = eventHistory.second

                    if (pastEventTimeMillis > lastEventTimeMillis) {
                        // pastEventTimeMillis 이 lastEventTimeMillis 이후
                        // 밀린 이벤트 재전송
                        try {
                            sseEmitter.send(pastEvent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        // 시간 지난 이미터, 이벤트 판별 및 제거
        val removeEmitterIdList = ArrayList<String>()
        for (emitter in emitterMap) {
            // 이미터 생성시간
            val emitterCreateTimeMillis = emitter.value.first

            // 현재시간
            val nowTimeMillis = System.currentTimeMillis()

            // 이미터 생성시간으로부터 몇 ms 지났는지
            val diffMs = nowTimeMillis - emitterCreateTimeMillis

            // 이미터 생성 시간이 타임아웃 시간(+n 밀리초) 을 초과했을 때 = 타임아웃이 되었는데도 갱신할 의지가 없다고 판단될 때
            if (diffMs > sseEmitterExpireTimeMs + sseEmitterSurviveTimeMs) {
                // 삭제 목록에 포함
                removeEmitterIdList.add(emitter.key)
            }
        }

        // 삭제 목록에 있는 이미터와 이벤트 삭제
        for (removeEmitterId in removeEmitterIdList) {
            emitterMap.remove(removeEmitterId)
            eventHistoryMap.remove(removeEmitterId)
        }

        return sseEmitter
    }


    // (저장된 모든 emitter 에 이벤트 발송)
    fun broadcastEvent(
        eventName: String,
        eventMessage: String
    ) {
        for (emitter in emitterMap) { // 저장된 모든 emitter 에 발송 (필터링 하려면 emitter.key 에 저장된 정보로 필터링 가능)
            executorService.execute {
                // 발송 시간
                val eventTimeMillis = System.currentTimeMillis()

                // 이벤트 고유값 생성 (이미터고유값/발송시간)
                val eventId = "${emitter.key}/${eventTimeMillis}"

                // 이벤트 빌더 생성
                val sseEventBuilder = SseEmitter
                    .event()
                    .id(eventId)
                    .name(eventName)
                    .data(eventMessage)

                // 이벤트 누락 방지 처리를 위하여 이벤트 빌더 기록
                if (eventHistoryMap.containsKey(emitter.key)) {
                    eventHistoryMap[emitter.key]!!.add(Pair(eventTimeMillis, sseEventBuilder))
                } else {
                    eventHistoryMap[emitter.key] = arrayListOf(Pair(eventTimeMillis, sseEventBuilder))
                }

                // 이벤트 발송
                try {
                    emitter.value.second.send(
                        sseEventBuilder
                    )
                } catch (_: Exception) {
                }
            }
        }
    }


    // (memberUidSet 에 속하는 모든 emitter 에 이벤트 발송)
    fun sendEventToMemberSet(
        eventName: String,
        eventMessage: String,
        memberUidSet: Set<Long?>
    ) {
        for (emitter in emitterMap) { // 저장된 모든 emitter 에 발송 (필터링 하려면 emitter.key 에 저장된 정보로 필터링 가능)
            executorService.execute {
                // emitterId (멤버고유번호(비회원은 "null")_객체 아이디 발행일_발행총개수) 에서 memberUid 추출
                val emitterIdSplit = emitter.key.split("_")
                val emitterMemberUid = emitterIdSplit[0] // 멤버고유번호(비회원은 "null")

                for (memberUid in memberUidSet) {
                    if (emitterMemberUid == memberUid.toString()) {
                        // 발송 시간
                        val eventTimeMillis = System.currentTimeMillis()

                        // 이벤트 고유값 생성 (이미터고유값/발송시간)
                        val eventId = "${emitter.key}/${eventTimeMillis}"

                        // 이벤트 빌더 생성
                        val sseEventBuilder = SseEmitter
                            .event()
                            .id(eventId)
                            .name(eventName)
                            .data(eventMessage)

                        // 이벤트 누락 방지 처리를 위하여 이벤트 빌더 기록
                        if (eventHistoryMap.containsKey(emitter.key)) {
                            eventHistoryMap[emitter.key]!!.add(Pair(eventTimeMillis, sseEventBuilder))
                        } else {
                            eventHistoryMap[emitter.key] = arrayListOf(Pair(eventTimeMillis, sseEventBuilder))
                        }

                        // 이벤트 발송
                        try {
                            emitter.value.second.send(
                                sseEventBuilder
                            )
                        } catch (_: Exception) {
                        }

                        break
                    }
                }
            }
        }
    }


    // (memberUid(비회원은 null) 에 속하는 emitter 에 이벤트 발송)
    fun sendEventToMember(
        eventName: String,
        eventMessage: String,
        memberUid: Long?
    ) {
        for (emitter in emitterMap) { // 저장된 모든 emitter 에 발송 (필터링 하려면 emitter.key 에 저장된 정보로 필터링 가능)
            executorService.execute {
                // emitterId (멤버고유번호(비회원은 "null")_객체 아이디 발행일_발행총개수) 에서 memberUid 추출
                val emitterIdSplit = emitter.key.split("_")
                val emitterMemberUid = emitterIdSplit[0] // 멤버고유번호(비회원은 "null")

                if (emitterMemberUid == memberUid.toString()) {
                    // 발송 시간
                    val eventTimeMillis = System.currentTimeMillis()

                    // 이벤트 고유값 생성 (이미터고유값/발송시간)
                    val eventId = "${emitter.key}/${eventTimeMillis}"

                    // 이벤트 빌더 생성
                    val sseEventBuilder = SseEmitter
                        .event()
                        .id(eventId)
                        .name(eventName)
                        .data(eventMessage)

                    // 이벤트 누락 방지 처리를 위하여 이벤트 빌더 기록
                    if (eventHistoryMap.containsKey(emitter.key)) {
                        eventHistoryMap[emitter.key]!!.add(Pair(eventTimeMillis, sseEventBuilder))
                    } else {
                        eventHistoryMap[emitter.key] = arrayListOf(Pair(eventTimeMillis, sseEventBuilder))
                    }

                    // 이벤트 발송
                    try {
                        emitter.value.second.send(
                            sseEventBuilder
                        )
                    } catch (_: Exception) {
                    }
                }
            }
        }
    }
}
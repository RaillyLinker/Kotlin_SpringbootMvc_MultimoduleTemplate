package com.raillylinker.module_common.classes

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore

// [스레드 병합 클래스]
/*
    사용 예시 :
    val threadMerger =
        ThreadMerger(
            3,
            onComplete = {
                screenDataSemaphoreMbr.release()
                onComplete()
            }
        )
    위와 같이 객체를 생성.
    threadMerger.threadComplete()
    위와 같이 각 스레드동작 완료시마다 호출하면 객체 생성시 설정한 갯수만큼 호출되면 onComplete 가 실행되고, 그 이상으로 호출하면 아무 반응 없음
 */
class ThreadMerger(
    // 병합할 스레드 총개수
    private val threadTotalCount: Int,
    // 스레드 병합이 모두 끝나면 실행할 콜백 함수
    private val onComplete: () -> Unit
) {
    // <멤버 변수 공간>
    // (스레드 풀)
    private val executorService: ExecutorService = Executors.newCachedThreadPool()

    // (현재 병합된 스레드 개수 및 세마포어)
    private var mergedThreadCount = 0
    private val mergedThreadCountSemaphore = Semaphore(1)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (스레드 병합 개수 +1)
    fun mergeThread() {
        executorService.execute {
            mergedThreadCountSemaphore.acquire()
            // 오버플로우 방지
            if (mergedThreadCount < 0) {
                mergedThreadCountSemaphore.release()
                return@execute
            }
            try {
                // 스레드 병합 카운트 +1
                ++mergedThreadCount
                if (mergedThreadCount == threadTotalCount) {
                    // 병합 카운트가 스레드 총 개수에 다다랐을 때
                    mergedThreadCountSemaphore.release()
                    onComplete()
                } else {
                    // 병합 카운트가 스레드 총 개수에 다다르지 못했을 때
                    mergedThreadCountSemaphore.release()
                }
            } catch (e: Exception) {
                mergedThreadCountSemaphore.release()
            }
        }
    }

    // (스레드 병합 개수 초기화)
    fun rewind() {
        mergedThreadCountSemaphore.acquire()
        try {
            mergedThreadCount = 0
        } finally {
            mergedThreadCountSemaphore.release()
        }
    }
}
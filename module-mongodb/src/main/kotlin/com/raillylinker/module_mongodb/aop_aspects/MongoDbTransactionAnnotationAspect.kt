package com.raillylinker.module_mongodb.aop_aspects

import com.raillylinker.module_mongodb.annotations.CustomMongoDbTransactional
import com.raillylinker.module_mongodb.const_objects.ModuleConst
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.context.ApplicationContext
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.DefaultTransactionDefinition

// [MongoDB @CustomMongoDbTransactional 어노테이션 함수 처리 AOP]
@Component
@Aspect
class MongoDbTransactionAnnotationAspect(
    private val applicationContext: ApplicationContext
) {
    companion object {
        // MongoDb 트랜젝션용 어노테이션인 CustomMongoDbTransactional 파일의 프로젝트 경로
        const val MONGO_DB_TRANSACTION_ANNOTATION_PATH =
            "@annotation(${ModuleConst.PACKAGE_NAME}.annotations.CustomMongoDbTransactional)"
    }


    // ---------------------------------------------------------------------------------------------
    // <AOP 작성 공간>
    // (@CustomTransactional 를 입력한 함수 실행 전후에 JPA 트랜젝션 적용)
    @Around(MONGO_DB_TRANSACTION_ANNOTATION_PATH)
    fun aroundMongoDbTransactionAnnotationFunction(joinPoint: ProceedingJoinPoint): Any? {
        val proceed: Any?

        // transactionManager and transactionStatus 리스트
        val transactionManagerAndTransactionStatusList =
            ArrayList<Pair<PlatformTransactionManager, TransactionStatus>>()

        try {
            val annotation = ((joinPoint.signature as MethodSignature).method).getAnnotation(
                CustomMongoDbTransactional::class.java
            )

            // annotation 에 설정된 transaction 순차 실행 및 저장
            for (transactionManagerBeanName in annotation.transactionManagerBeanNameList) {
                // annotation 에 저장된 transactionManager Bean 이름으로 Bean 객체 가져오기
                val platformTransactionManager =
                    applicationContext.getBean(transactionManagerBeanName) as PlatformTransactionManager

                // CustomTransactional 에서 읽기 전용 속성 확인
                val isReadOnly = annotation.readOnly

                // readOnly 설정을 transactionDefinition 에 적용
                val transactionDefinition = DefaultTransactionDefinition()
                transactionDefinition.isReadOnly = isReadOnly

                // transaction 시작 및 정보 저장
                transactionManagerAndTransactionStatusList.add(
                    Pair(
                        platformTransactionManager,
                        platformTransactionManager.getTransaction(transactionDefinition)
                    )
                )
            }

            //// 함수 실행 전
            proceed = joinPoint.proceed() // 함수 실행
            //// 함수 실행 후

            // annotation 에 설정된 transaction commit 역순 실행 및 저장
            for (transactionManagerIdx in transactionManagerAndTransactionStatusList.size - 1 downTo 0) {
                val transactionManager = transactionManagerAndTransactionStatusList[transactionManagerIdx]
                transactionManager.first.commit(transactionManager.second)
            }
        } catch (e: Exception) {
            // annotation 에 설정된 transaction rollback 역순 실행 및 저장
            for (transactionManagerIdx in transactionManagerAndTransactionStatusList.size - 1 downTo 0) {
                val transactionManager = transactionManagerAndTransactionStatusList[transactionManagerIdx]
                transactionManager.first.rollback(transactionManager.second)
            }
            throw e
        }

        return proceed // 결과 리턴
    }

    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>


    // ---------------------------------------------------------------------------------------------
    // <비공개 메소드 공간>


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
}
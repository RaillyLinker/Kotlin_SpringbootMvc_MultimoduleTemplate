package com.raillylinker.module_mongodb.annotations

// [MongoDB 트랜젝션 어노테이션]
// MongoDB 에서 트랜젝션을 사용하려면 MongoDB Replica Set 설정을 하여 실행시키는 환경에서만 가능합니다.
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CustomMongoDbTransactional(
    val transactionManagerBeanNameList: Array<String>
)
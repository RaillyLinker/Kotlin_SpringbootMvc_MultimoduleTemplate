package com.raillylinker.module_jpa.jpa_beans.db1_main.repositories

import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_Service1AddEmailVerificationData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

// (JPA 레포지토리)
// : 함수 작성 명명법에 따라 데이터베이스 SQL 동작을 자동지원
@Repository
interface Db1_RaillyLinkerCompany_Service1AddEmailVerificationData_Repository :
    JpaRepository<Db1_RaillyLinkerCompany_Service1AddEmailVerificationData, Long> {
}
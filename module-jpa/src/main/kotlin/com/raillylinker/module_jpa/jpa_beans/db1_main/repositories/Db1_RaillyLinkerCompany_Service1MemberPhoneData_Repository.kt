package com.raillylinker.module_jpa.jpa_beans.db1_main.repositories

import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_Service1MemberData
import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_Service1MemberPhoneData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

// (JPA 레포지토리)
// : 함수 작성 명명법에 따라 데이터베이스 SQL 동작을 자동지원
@Repository
interface Db1_RaillyLinkerCompany_Service1MemberPhoneData_Repository :
    JpaRepository<Db1_RaillyLinkerCompany_Service1MemberPhoneData, Long> {
    fun findByPhoneNumberAndRowDeleteDateStr(
        phoneNumber: String,
        rowDeleteDateStr: String
    ): Db1_RaillyLinkerCompany_Service1MemberPhoneData?

    fun existsByPhoneNumberAndRowDeleteDateStr(
        phoneNumber: String,
        rowDeleteDateStr: String
    ): Boolean

    fun findAllByService1MemberDataAndRowDeleteDateStr(
        service1MemberData: Db1_RaillyLinkerCompany_Service1MemberData,
        rowDeleteDateStr: String
    ): List<Db1_RaillyLinkerCompany_Service1MemberPhoneData>

    fun existsByService1MemberDataAndRowDeleteDateStr(
        service1MemberData: Db1_RaillyLinkerCompany_Service1MemberData,
        rowDeleteDateStr: String
    ): Boolean
}
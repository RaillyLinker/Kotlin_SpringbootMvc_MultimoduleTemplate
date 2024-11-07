package com.raillylinker.module_jpa.jpa_beans.db1_main.repositories

import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_Service1LogInTokenHistory
import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_Service1MemberData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

// (JPA 레포지토리)
// : 함수 작성 명명법에 따라 데이터베이스 SQL 동작을 자동지원
@Repository
interface Db1_RaillyLinkerCompany_Service1LogInTokenHistory_Repository :
    JpaRepository<Db1_RaillyLinkerCompany_Service1LogInTokenHistory, Long> {
    fun findByTokenTypeAndAccessTokenAndLogoutDateAndRowDeleteDateStr(
        tokenType: String,
        accessToken: String,
        logoutDate: LocalDateTime?,
        rowDeleteDateStr: String
    ): Db1_RaillyLinkerCompany_Service1LogInTokenHistory?

    fun findAllByService1MemberDataAndLogoutDateAndRowDeleteDateStr(
        service1MemberData: Db1_RaillyLinkerCompany_Service1MemberData,
        logoutDate: LocalDateTime?,
        rowDeleteDateStr: String
    ): List<Db1_RaillyLinkerCompany_Service1LogInTokenHistory>

    fun findAllByService1MemberDataAndAccessTokenExpireWhenAfterAndRowDeleteDateStr(
        service1MemberData: Db1_RaillyLinkerCompany_Service1MemberData,
        accessTokenExpireWhenAfter: LocalDateTime,
        rowDeleteDateStr: String
    ): List<Db1_RaillyLinkerCompany_Service1LogInTokenHistory>
}
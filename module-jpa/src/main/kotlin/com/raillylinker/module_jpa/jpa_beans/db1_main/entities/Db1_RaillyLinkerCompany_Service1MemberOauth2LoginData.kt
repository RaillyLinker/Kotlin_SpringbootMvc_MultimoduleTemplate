package com.raillylinker.module_jpa.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "service1_member_oauth2_login_data",
    catalog = "railly_linker_company",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["oauth2_type_code", "oauth2_id", "row_delete_date_str"])
    ]
)
@Comment("Service1 계정 회원의 OAuth2 로그인 정보 테이블")
class Db1_RaillyLinkerCompany_Service1MemberOauth2LoginData(
    @ManyToOne
    @JoinColumn(name = "service1_member_uid", nullable = false)
    @Comment("멤버 고유번호(railly_linker_company.service1_member_data.uid)")
    var service1MemberData: Db1_RaillyLinkerCompany_Service1MemberData,

    @Column(name = "oauth2_type_code", nullable = false, columnDefinition = "TINYINT UNSIGNED")
    @Comment("oauth2 종류 (1 : GOOGLE, 2 : NAVER, 3 : KAKAO, 4 : APPLE)")
    var oauth2TypeCode: Byte,

    @Column(name = "oauth2_id", nullable = false, columnDefinition = "VARCHAR(50)")
    @Comment("OAuth2 로그인으로 얻어온 고유값")
    var oauth2Id: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", columnDefinition = "BIGINT UNSIGNED")
    @Comment("행 고유값")
    var uid: Long? = null

    @Column(name = "row_create_date", nullable = false, columnDefinition = "DATETIME(3)")
    @CreationTimestamp
    @Comment("행 생성일")
    var rowCreateDate: LocalDateTime? = null

    @Column(name = "row_update_date", nullable = false, columnDefinition = "DATETIME(3)")
    @UpdateTimestamp
    @Comment("행 수정일")
    var rowUpdateDate: LocalDateTime? = null

    @Column(name = "row_delete_date_str", nullable = false, columnDefinition = "VARCHAR(50)")
    @ColumnDefault("'/'")
    @Comment("행 삭제일(yyyy_MM_dd_T_HH_mm_ss_SSS_z, 삭제되지 않았다면 /)")
    var rowDeleteDateStr: String = "/"


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}
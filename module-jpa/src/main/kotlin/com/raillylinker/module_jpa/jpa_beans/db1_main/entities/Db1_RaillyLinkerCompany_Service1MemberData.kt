package com.raillylinker.module_jpa.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "service1_member_data",
    catalog = "railly_linker_company",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["account_id", "row_delete_date_str"])
    ]
)
@Comment("Service1 계정 회원 정보 테이블")
class Db1_RaillyLinkerCompany_Service1MemberData(
    @Column(name = "account_id", nullable = false, columnDefinition = "VARCHAR(100)")
    @Comment("계정 아이디")
    var accountId: String,

    @Column(name = "account_password", nullable = true, columnDefinition = "VARCHAR(100)")
    @Comment("계정 로그인시 사용하는 비밀번호 (계정 아이디, 이메일, 전화번호 로그인에 모두 사용됨. OAuth2 만 등록했다면 null)")
    var accountPassword: String?,

    @ManyToOne
    @JoinColumn(name = "front_service1_member_profile_uid", nullable = true)
    @Comment("대표 프로필 Uid (railly_linker_company.service1_member_profile_data.uid)")
    var frontService1MemberProfileData: Db1_RaillyLinkerCompany_Service1MemberProfileData?,

    @ManyToOne
    @JoinColumn(name = "front_service1_member_email_uid", nullable = true)
    @Comment("대표 이메일 Uid (railly_linker_company.service1_member_email_data.uid)")
    var frontService1MemberEmailData: Db1_RaillyLinkerCompany_Service1MemberEmailData?,

    @ManyToOne
    @JoinColumn(name = "front_service1_member_phone_uid", nullable = true)
    @Comment("대표 전화번호 Uid (railly_linker_company.service1_member_phone_data.uid)")
    var frontService1MemberPhoneData: Db1_RaillyLinkerCompany_Service1MemberPhoneData?
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

    @OneToMany(mappedBy = "service1MemberData", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var service1MemberRoleDataList: MutableList<Db1_RaillyLinkerCompany_Service1MemberRoleData> = mutableListOf()

    @OneToMany(mappedBy = "service1MemberData", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var service1MemberProfileDataList: MutableList<Db1_RaillyLinkerCompany_Service1MemberProfileData> = mutableListOf()

    @OneToMany(mappedBy = "service1MemberData", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var service1MemberPhoneDataList: MutableList<Db1_RaillyLinkerCompany_Service1MemberPhoneData> = mutableListOf()

    @OneToMany(mappedBy = "service1MemberData", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var service1MemberOauth2LoginDataList: MutableList<Db1_RaillyLinkerCompany_Service1MemberOauth2LoginData> = mutableListOf()

    @OneToMany(mappedBy = "service1MemberData", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var service1MemberEmailDataList: MutableList<Db1_RaillyLinkerCompany_Service1MemberEmailData> = mutableListOf()

    @OneToMany(mappedBy = "service1MemberData", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var service1MemberLockHistoryList: MutableList<Db1_RaillyLinkerCompany_Service1MemberLockHistory> = mutableListOf()

    @OneToMany(mappedBy = "service1MemberData", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var service1LogInTokenHistoryList: MutableList<Db1_RaillyLinkerCompany_Service1LogInTokenHistory> = mutableListOf()

    @OneToMany(mappedBy = "service1MemberData", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var service1AddPhoneNumberVerificationDataList: MutableList<Db1_RaillyLinkerCompany_Service1AddPhoneNumberVerificationData> =
        mutableListOf()

    @OneToMany(mappedBy = "service1MemberData", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var service1AddEmailVerificationDataList: MutableList<Db1_RaillyLinkerCompany_Service1AddEmailVerificationData> = mutableListOf()


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}
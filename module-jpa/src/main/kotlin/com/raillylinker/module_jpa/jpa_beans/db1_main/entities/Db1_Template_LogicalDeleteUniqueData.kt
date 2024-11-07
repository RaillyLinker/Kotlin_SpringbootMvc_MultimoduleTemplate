package com.raillylinker.module_jpa.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

/*
     - 본 테이블은 논리적 삭제를 적용한 테이블에 Unique 변수를 적용하는 방법을 설명하기 위한 샘플입니다.
         논리적 삭제시, 데이터에서 행이 삭제될 일은 없다고 보면 되므로,
         변수에 단순하게 unique 를 걸어둔다면, 해당 행이 비활성 상태일 때에도, 새로운 행으로 동일한 값을 가질 수 없게 됩니다.
         이에, unique 를 적용할 변수와 더불어, 행의 삭제일을 나타내는 row_delete_date_str 변수를 같이 묶어 unique 를 걸어둠으로써,
         삭제시에는 row_delete_date_str 가 현재 시간으로 매번 달라지기에 문제가 없고,
         생성시에는 기존 행들 중, 행 활성화 상태를 뜻하는 row_delete_date_str 가 "/" 인 경우가 없다면 문제가 없게 됩니다.
 */

// 주의 : 낙관적 Lock (@Version) 사용시 Transaction 기능과 충돌이 있음
@Entity
@Table(
    name = "logical_delete_unique_data",
    catalog = "template",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["unique_value", "row_delete_date_str"])
    ]
)
@Comment("논리적 삭제 유니크 제약 테스트 테이블")
class Db1_Template_LogicalDeleteUniqueData(
    @Column(name = "unique_value", nullable = false, columnDefinition = "INT")
    @Comment("유니크 값")
    var uniqueValue: Int
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
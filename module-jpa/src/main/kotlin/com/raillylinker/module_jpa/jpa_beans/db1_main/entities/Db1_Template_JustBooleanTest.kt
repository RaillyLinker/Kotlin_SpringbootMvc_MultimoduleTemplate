package com.raillylinker.module_jpa.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(
    name = "just_boolean_test",
    catalog = "template"
)
@Comment("Boolean 값 반환 예시만을 위한 테이블")
class Db1_Template_JustBooleanTest(
    @Column(name = "bool_value", nullable = false, columnDefinition = "BIT(1)")
    @Comment("bool 값")
    var boolValue: Boolean
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", columnDefinition = "BIGINT UNSIGNED")
    @Comment("행 고유값")
    var uid: Long? = null


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}
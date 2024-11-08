package com.raillylinker.module_jpa.jpa_beans.db1_main.repositories_dsl

import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.Db1_Template_FkTestManyToOneChild
import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.Db1_Template_FkTestParent

interface Db1_Template_RepositoryDsl {
    // 부모 테이블과 자식 테이블을 조인하여 조회하는 예시
    fun findParentWithChildren(): List<Db1_Template_FkTestParent>

    // 특정 조건으로 부모-자식 조회 (예: 부모 이름으로 필터링)
    fun findParentWithChildrenByName(parentName: String): List<Db1_Template_FkTestParent>

    // 부모-자식 테이블의 특정 자식 데이터 조회
    fun findChildByParentId(parentId: Long): List<Db1_Template_FkTestManyToOneChild>
}
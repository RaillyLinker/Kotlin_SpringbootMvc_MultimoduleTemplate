package com.raillylinker.module_jpa.jpa_beans.db1_main.repositories_dsl.impls

import com.querydsl.jpa.impl.JPAQueryFactory
import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.Db1_Template_FkTestManyToOneChild
import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.Db1_Template_FkTestParent
import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.QDb1_Template_FkTestManyToOneChild.db1_Template_FkTestManyToOneChild
import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.QDb1_Template_FkTestParent.db1_Template_FkTestParent
import com.raillylinker.module_jpa.jpa_beans.db1_main.repositories_dsl.Db1_Template_RepositoryDsl
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class Db1_Template_RepositoryDslImpl(entityManager: EntityManager) : Db1_Template_RepositoryDsl {
    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)

    // 부모 테이블과 자식 테이블을 조인하여 조회하는 예시
    override fun findParentWithChildren(): List<Db1_Template_FkTestParent> {
        return queryFactory
            .selectFrom(db1_Template_FkTestParent)
            .leftJoin(db1_Template_FkTestParent.fkTestManyToOneChildList, db1_Template_FkTestManyToOneChild)
            .fetchJoin() // fetchJoin을 사용하여 자식 엔티티를 함께 가져옴
            .fetch() // 결과를 가져옴
    }

    // 특정 조건으로 부모-자식 조회 (예: 부모 이름으로 필터링)
    override fun findParentWithChildrenByName(parentName: String): List<Db1_Template_FkTestParent> {
        return queryFactory
            .selectFrom(db1_Template_FkTestParent)
            .leftJoin(db1_Template_FkTestParent.fkTestManyToOneChildList, db1_Template_FkTestManyToOneChild)
            .fetchJoin()
            .where(db1_Template_FkTestParent.parentName.eq(parentName))
            .fetch()
    }

    // 부모-자식 테이블의 특정 자식 데이터 조회
    override fun findChildByParentId(parentId: Long): List<Db1_Template_FkTestManyToOneChild> {
        return queryFactory
            .selectFrom(db1_Template_FkTestManyToOneChild)
            .leftJoin(db1_Template_FkTestManyToOneChild.fkTestParent, db1_Template_FkTestParent)
            .where(db1_Template_FkTestParent.uid.eq(parentId))
            .fetch()
    }
}
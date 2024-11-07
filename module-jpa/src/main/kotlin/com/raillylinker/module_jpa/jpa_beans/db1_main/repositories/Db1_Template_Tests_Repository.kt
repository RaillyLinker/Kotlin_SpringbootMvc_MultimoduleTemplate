package com.raillylinker.module_jpa.jpa_beans.db1_main.repositories

import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.Db1_Template_TestData
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.data.repository.query.Param;

@Repository
interface Db1_Template_Tests_Repository : JpaRepository<Db1_Template_TestData, Long> {
    fun findAllByRowDeleteDateStrOrderByRowCreateDate(
        rowDeleteDateStr: String,
        pageable: Pageable
    ): Page<Db1_Template_TestData>

    fun countByRowDeleteDateStr(
        rowDeleteDateStr: String
    ): Long

    fun findByUidAndRowDeleteDateStr(
        uid: Long,
        rowDeleteDateStr: String
    ): Db1_Template_TestData?

    fun findAllByRowDeleteDateStrOrderByRowCreateDate(
        rowDeleteDateStr: String
    ): List<Db1_Template_TestData>

    fun findAllByRowDeleteDateStrNotOrderByRowCreateDate(
        rowDeleteDateStr: String
    ): List<Db1_Template_TestData>

    fun findAllByContentOrderByRowCreateDate(
        content: String
    ): List<Db1_Template_TestData>

    @Query(
        """
        SELECT 
        template_test_data 
        FROM 
        Db1_Template_TestData AS template_test_data 
        WHERE 
        template_test_data.content = :content 
        order by 
        template_test_data.rowCreateDate desc
    """
    )
    fun findAllByContentOrderByRowCreateDateJpql(
        @Param("content") content: String
    ): List<Db1_Template_TestData>
}
package com.raillylinker.module_jpa.jpa_beans.db1_main.repositories

import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.Db1_Template_LogicalDeleteUniqueData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface Db1_Template_LogicalDeleteUniqueData_Repository :
    JpaRepository<Db1_Template_LogicalDeleteUniqueData, Long> {
    fun findByUidAndRowDeleteDateStr(
        uid: Long,
        rowDeleteDateStr: String
    ): Db1_Template_LogicalDeleteUniqueData?

    fun findAllByRowDeleteDateStrOrderByRowCreateDate(
        rowDeleteDateStr: String
    ): List<Db1_Template_LogicalDeleteUniqueData>

    fun findAllByRowDeleteDateStrNotOrderByRowCreateDate(
        rowDeleteDateStr: String
    ): List<Db1_Template_LogicalDeleteUniqueData>

    fun findByUniqueValueAndRowDeleteDateStr(
        uniqueValue: Int,
        rowDeleteDateStr: String
    ): Db1_Template_LogicalDeleteUniqueData?
}
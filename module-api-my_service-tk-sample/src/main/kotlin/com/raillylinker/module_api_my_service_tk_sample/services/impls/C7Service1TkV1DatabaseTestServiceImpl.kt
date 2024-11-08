package com.raillylinker.module_api_my_service_tk_sample.services.impls

import com.raillylinker.module_api_my_service_tk_sample.controllers.C7Service1TkV1DatabaseTestController
import com.raillylinker.module_api_my_service_tk_sample.services.C7Service1TkV1DatabaseTestService
import com.raillylinker.module_jpa.annotations.CustomTransactional
import com.raillylinker.module_jpa.configurations.jpa_configs.Db1MainConfig
import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.*
import com.raillylinker.module_jpa.jpa_beans.db1_main.repositories.*
import com.raillylinker.module_jpa.jpa_beans.db1_main.repositories_dsl.Db1_Template_RepositoryDsl
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class C7Service1TkV1DatabaseTestServiceImpl(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,

    // (Database Repository)
    private val db1NativeRepository: Db1_Native_Repository,
    private val db1TemplateTestsRepository: Db1_Template_Tests_Repository,
    private val db1TemplateFkTestParentRepository: Db1_Template_FkTestParent_Repository,
    private val db1TemplateFkTestManyToOneChildRepository: Db1_Template_FkTestManyToOneChild_Repository,
    private val db1TemplateLogicalDeleteUniqueDataRepository: Db1_Template_LogicalDeleteUniqueData_Repository,
    private val db1TemplateJustBooleanTestRepository: Db1_Template_JustBooleanTest_Repository,

    // (Database Repository DSL)
    private val db1TemplateRepositoryDsl: Db1_Template_RepositoryDsl
) : C7Service1TkV1DatabaseTestService {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api1InsertDataSample(
        httpServletResponse: HttpServletResponse,
        inputVo: C7Service1TkV1DatabaseTestController.Api1InsertDataSampleInputVo
    ): C7Service1TkV1DatabaseTestController.Api1InsertDataSampleOutputVo? {
        val result = db1TemplateTestsRepository.save(
            Db1_Template_TestData(
                inputVo.content,
                (0..99999999).random(),
                ZonedDateTime.parse(inputVo.dateString, DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                    .toLocalDateTime()
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api1InsertDataSampleOutputVo(
            result.uid!!,
            result.content,
            result.randomNum,
            result.testDatetime.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowCreateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowDeleteDateStr
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api2DeleteRowsSample(httpServletResponse: HttpServletResponse, deleteLogically: Boolean) {
        if (deleteLogically) {
            val entityList = db1TemplateTestsRepository.findAllByRowDeleteDateStrOrderByRowCreateDate("/")
            for (entity in entityList) {
                entity.rowDeleteDateStr =
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                db1TemplateTestsRepository.save(entity)
            }
        } else {
            db1TemplateTestsRepository.deleteAll()
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api3DeleteRowSample(httpServletResponse: HttpServletResponse, index: Long, deleteLogically: Boolean) {
        val entity = db1TemplateTestsRepository.findByUidAndRowDeleteDateStr(index, "/")

        if (entity == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (deleteLogically) {
            entity.rowDeleteDateStr =
                LocalDateTime.now().atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
            db1TemplateTestsRepository.save(entity)
        } else {
            db1TemplateTestsRepository.deleteById(index)
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun api4SelectRowsSample(httpServletResponse: HttpServletResponse): C7Service1TkV1DatabaseTestController.Api4SelectRowsSampleOutputVo? {
        val resultEntityList =
            db1TemplateTestsRepository.findAllByRowDeleteDateStrOrderByRowCreateDate("/")
        val entityVoList = ArrayList<C7Service1TkV1DatabaseTestController.Api4SelectRowsSampleOutputVo.TestEntityVo>()
        for (resultEntity in resultEntityList) {
            entityVoList.add(
                C7Service1TkV1DatabaseTestController.Api4SelectRowsSampleOutputVo.TestEntityVo(
                    resultEntity.uid!!,
                    resultEntity.content,
                    resultEntity.randomNum,
                    resultEntity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowDeleteDateStr
                )
            )
        }

        val logicalDeleteEntityVoList =
            db1TemplateTestsRepository.findAllByRowDeleteDateStrNotOrderByRowCreateDate("/")
        val logicalDeleteVoList =
            ArrayList<C7Service1TkV1DatabaseTestController.Api4SelectRowsSampleOutputVo.TestEntityVo>()
        for (resultEntity in logicalDeleteEntityVoList) {
            logicalDeleteVoList.add(
                C7Service1TkV1DatabaseTestController.Api4SelectRowsSampleOutputVo.TestEntityVo(
                    resultEntity.uid!!,
                    resultEntity.content,
                    resultEntity.randomNum,
                    resultEntity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowDeleteDateStr
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api4SelectRowsSampleOutputVo(
            entityVoList,
            logicalDeleteVoList
        )
    }


    ////
    override fun api5SelectRowsOrderByRandomNumSample(
        httpServletResponse: HttpServletResponse,
        num: Int
    ): C7Service1TkV1DatabaseTestController.Api5SelectRowsOrderByRandomNumSampleOutputVo? {
        val foundEntityList = db1NativeRepository.forC7N5(num)

        val testEntityVoList =
            ArrayList<C7Service1TkV1DatabaseTestController.Api5SelectRowsOrderByRandomNumSampleOutputVo.TestEntityVo>()

        for (entity in foundEntityList) {
            testEntityVoList.add(
                C7Service1TkV1DatabaseTestController.Api5SelectRowsOrderByRandomNumSampleOutputVo.TestEntityVo(
                    entity.uid,
                    entity.content,
                    entity.randomNum,
                    entity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.rowCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.rowUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.distance
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api5SelectRowsOrderByRandomNumSampleOutputVo(
            testEntityVoList
        )
    }


    ////
    override fun api6SelectRowsOrderByRowCreateDateSample(
        httpServletResponse: HttpServletResponse,
        dateString: String
    ): C7Service1TkV1DatabaseTestController.Api6SelectRowsOrderByRowCreateDateSampleOutputVo? {
        val foundEntityList = db1NativeRepository.forC7N6(
            ZonedDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                .toLocalDateTime()
        )

        val testEntityVoList =
            ArrayList<C7Service1TkV1DatabaseTestController.Api6SelectRowsOrderByRowCreateDateSampleOutputVo.TestEntityVo>()

        for (entity in foundEntityList) {
            testEntityVoList.add(
                C7Service1TkV1DatabaseTestController.Api6SelectRowsOrderByRowCreateDateSampleOutputVo.TestEntityVo(
                    entity.uid,
                    entity.content,
                    entity.randomNum,
                    entity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.rowCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.rowUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.timeDiffMicroSec
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api6SelectRowsOrderByRowCreateDateSampleOutputVo(
            testEntityVoList
        )
    }


    ////
    override fun api7SelectRowsPageSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int
    ): C7Service1TkV1DatabaseTestController.Api7SelectRowsPageSampleOutputVo? {
        val pageable: Pageable = PageRequest.of(page - 1, pageElementsCount)
        val entityList = db1TemplateTestsRepository.findAllByRowDeleteDateStrOrderByRowCreateDate(
            "/",
            pageable
        )

        val testEntityVoList =
            ArrayList<C7Service1TkV1DatabaseTestController.Api7SelectRowsPageSampleOutputVo.TestEntityVo>()
        for (entity in entityList) {
            testEntityVoList.add(
                C7Service1TkV1DatabaseTestController.Api7SelectRowsPageSampleOutputVo.TestEntityVo(
                    entity.uid!!,
                    entity.content,
                    entity.randomNum,
                    entity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api7SelectRowsPageSampleOutputVo(
            entityList.totalElements,
            testEntityVoList
        )
    }


    ////
    override fun api8SelectRowsNativeQueryPageSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int,
        num: Int
    ): C7Service1TkV1DatabaseTestController.Api8SelectRowsNativeQueryPageSampleOutputVo? {
        val pageable: Pageable = PageRequest.of(page - 1, pageElementsCount)
        val voList = db1NativeRepository.forC7N8(
            num,
            pageable
        )

        val testEntityVoList =
            ArrayList<C7Service1TkV1DatabaseTestController.Api8SelectRowsNativeQueryPageSampleOutputVo.TestEntityVo>()
        for (vo in voList) {
            testEntityVoList.add(
                C7Service1TkV1DatabaseTestController.Api8SelectRowsNativeQueryPageSampleOutputVo.TestEntityVo(
                    vo.uid,
                    vo.content,
                    vo.randomNum,
                    vo.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.rowCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.rowUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.distance
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api8SelectRowsNativeQueryPageSampleOutputVo(
            voList.totalElements,
            testEntityVoList
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api9UpdateRowSample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long,
        inputVo: C7Service1TkV1DatabaseTestController.Api9UpdateRowSampleInputVo
    ): C7Service1TkV1DatabaseTestController.Api9UpdateRowSampleOutputVo? {
        val oldEntity = db1TemplateTestsRepository.findByUidAndRowDeleteDateStr(testTableUid, "/")

        if (oldEntity == null || oldEntity.rowDeleteDateStr != "/") {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        oldEntity.content = inputVo.content
        oldEntity.testDatetime =
            ZonedDateTime.parse(inputVo.dateString, DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                .toLocalDateTime()

        val result = db1TemplateTestsRepository.save(oldEntity)

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api9UpdateRowSampleOutputVo(
            result.uid!!,
            result.content,
            result.randomNum,
            result.testDatetime.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowCreateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api10UpdateRowNativeQuerySample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long,
        inputVo: C7Service1TkV1DatabaseTestController.Api10UpdateRowNativeQuerySampleInputVo
    ) {
        // !! 아래는 네이티브 쿼리로 수정하는 예시를 보인 것으로,
        // 이 경우에는 @UpdateTimestamp, @Version 기능이 자동 적용 되지 않습니다.
        // 고로 수정문은 jpa 를 사용하길 권장합니다. !!
        val testEntity = db1TemplateTestsRepository.findByUidAndRowDeleteDateStr(testTableUid, "/")

        if (testEntity == null || testEntity.rowDeleteDateStr != "/") {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            // 트랜젝션 커밋
            return
        }

        db1NativeRepository.forC7N10(
            testTableUid,
            inputVo.content,
            ZonedDateTime.parse(inputVo.dateString, DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                .toLocalDateTime()
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun api11SelectRowWhereSearchingKeywordSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int,
        searchKeyword: String
    ): C7Service1TkV1DatabaseTestController.Api11SelectRowWhereSearchingKeywordSampleOutputVo? {
        val pageable: Pageable = PageRequest.of(page - 1, pageElementsCount)
        val voList = db1NativeRepository.forC7N11(
            searchKeyword,
            pageable
        )

        val testEntityVoList =
            ArrayList<C7Service1TkV1DatabaseTestController.Api11SelectRowWhereSearchingKeywordSampleOutputVo.TestEntityVo>()
        for (vo in voList) {
            testEntityVoList.add(
                C7Service1TkV1DatabaseTestController.Api11SelectRowWhereSearchingKeywordSampleOutputVo.TestEntityVo(
                    vo.uid,
                    vo.content,
                    vo.randomNum,
                    vo.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.rowCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.rowUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api11SelectRowWhereSearchingKeywordSampleOutputVo(
            voList.totalElements,
            testEntityVoList
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api12TransactionTest(
        httpServletResponse: HttpServletResponse
    ) {
        db1TemplateTestsRepository.save(
            Db1_Template_TestData(
                "error test",
                (0..99999999).random(),
                LocalDateTime.now()
            )
        )

        throw Exception("Transaction Rollback Test!")
    }


    ////
    override fun api13NonTransactionTest(httpServletResponse: HttpServletResponse) {
        db1TemplateTestsRepository.save(
            Db1_Template_TestData(
                "error test",
                (0..99999999).random(),
                LocalDateTime.now()
            )
        )

        throw Exception("No Transaction Exception Test!")
    }

    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api13Dot1TryCatchNonTransactionTest(httpServletResponse: HttpServletResponse) {
        // @CustomTransactional 이 붙어있고, Exception 이 발생해도, 함수 내에서 try catch 로 처리하여 함수 외부로는 전파되지 않기에,
        // 트랜젝션 롤백이 발생하지 않습니다.
        try {
            db1TemplateTestsRepository.save(
                Db1_Template_TestData(
                    "error test",
                    (0..99999999).random(),
                    LocalDateTime.now()
                )
            )

            throw Exception("Transaction Rollback Test!")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    ////
    override fun api14SelectRowsNoDuplicatePagingSample(
        httpServletResponse: HttpServletResponse,
        lastItemUid: Long?,
        pageElementsCount: Int
    ): C7Service1TkV1DatabaseTestController.Api14SelectRowsNoDuplicatePagingSampleOutputVo? {
        // 중복 없는 페이징 쿼리를 사용합니다.
        val voList = db1NativeRepository.forC7N14(
            lastItemUid,
            pageElementsCount
        )

        // 전체 개수 카운팅은 따로 해주어야 합니다.
        val count = db1NativeRepository.forC7N14I1()

        val testEntityVoList =
            ArrayList<C7Service1TkV1DatabaseTestController.Api14SelectRowsNoDuplicatePagingSampleOutputVo.TestEntityVo>()
        for (vo in voList) {
            testEntityVoList.add(
                C7Service1TkV1DatabaseTestController.Api14SelectRowsNoDuplicatePagingSampleOutputVo.TestEntityVo(
                    vo.uid,
                    vo.content,
                    vo.randomNum,
                    vo.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.rowCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.rowUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api14SelectRowsNoDuplicatePagingSampleOutputVo(
            count,
            testEntityVoList
        )
    }


    ////
    override fun api15SelectRowsCountSample(httpServletResponse: HttpServletResponse): C7Service1TkV1DatabaseTestController.Api15SelectRowsCountSampleOutputVo? {
        val count = db1TemplateTestsRepository.countByRowDeleteDateStr("/")

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api15SelectRowsCountSampleOutputVo(count)
    }


    ////
    override fun api16SelectRowsCountByNativeQuerySample(httpServletResponse: HttpServletResponse): C7Service1TkV1DatabaseTestController.Api16SelectRowsCountByNativeQuerySampleOutputVo? {
        val count = db1NativeRepository.forC7N16()

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api16SelectRowsCountByNativeQuerySampleOutputVo(count)
    }


    ////
    override fun api17SelectRowByNativeQuerySample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long
    ): C7Service1TkV1DatabaseTestController.Api17SelectRowByNativeQuerySampleOutputVo? {
        val entity = db1NativeRepository.forC7N17(testTableUid)

        if (entity == null) {
            httpServletResponse.status = HttpStatus.OK.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api17SelectRowByNativeQuerySampleOutputVo(
            entity.uid,
            entity.content,
            entity.randomNum,
            entity.testDatetime.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            entity.rowCreateDate.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            entity.rowUpdateDate.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api18InsertUniqueTestTableRowSample(
        httpServletResponse: HttpServletResponse,
        inputVo: C7Service1TkV1DatabaseTestController.Api18InsertUniqueTestTableRowSampleInputVo
    ): C7Service1TkV1DatabaseTestController.Api18InsertUniqueTestTableRowSampleOutputVo? {
        val result = db1TemplateLogicalDeleteUniqueDataRepository.save(
            Db1_Template_LogicalDeleteUniqueData(
                inputVo.uniqueValue
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api18InsertUniqueTestTableRowSampleOutputVo(
            result.uid!!,
            result.uniqueValue,
            result.rowCreateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowDeleteDateStr
        )
    }


    ////
    override fun api19SelectUniqueTestTableRowsSample(httpServletResponse: HttpServletResponse): C7Service1TkV1DatabaseTestController.Api19SelectUniqueTestTableRowsSampleOutputVo? {
        val resultEntityList =
            db1TemplateLogicalDeleteUniqueDataRepository.findAllByRowDeleteDateStrOrderByRowCreateDate("/")
        val entityVoList =
            ArrayList<C7Service1TkV1DatabaseTestController.Api19SelectUniqueTestTableRowsSampleOutputVo.TestEntityVo>()
        for (resultEntity in resultEntityList) {
            entityVoList.add(
                C7Service1TkV1DatabaseTestController.Api19SelectUniqueTestTableRowsSampleOutputVo.TestEntityVo(
                    resultEntity.uid!!,
                    resultEntity.uniqueValue,
                    resultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowDeleteDateStr
                )
            )
        }

        val logicalDeleteEntityVoList =
            db1TemplateLogicalDeleteUniqueDataRepository.findAllByRowDeleteDateStrNotOrderByRowCreateDate("/")
        val logicalDeleteVoList =
            ArrayList<C7Service1TkV1DatabaseTestController.Api19SelectUniqueTestTableRowsSampleOutputVo.TestEntityVo>()
        for (resultEntity in logicalDeleteEntityVoList) {
            logicalDeleteVoList.add(
                C7Service1TkV1DatabaseTestController.Api19SelectUniqueTestTableRowsSampleOutputVo.TestEntityVo(
                    resultEntity.uid!!,
                    resultEntity.uniqueValue,
                    resultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowDeleteDateStr
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api19SelectUniqueTestTableRowsSampleOutputVo(
            entityVoList,
            logicalDeleteVoList
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api20UpdateUniqueTestTableRowSample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long,
        inputVo: C7Service1TkV1DatabaseTestController.Api20UpdateUniqueTestTableRowSampleInputVo
    ): C7Service1TkV1DatabaseTestController.Api20UpdateUniqueTestTableRowSampleOutputVo? {
        val oldEntity =
            db1TemplateLogicalDeleteUniqueDataRepository.findByUidAndRowDeleteDateStr(testTableUid, "/")

        if (oldEntity == null || oldEntity.rowDeleteDateStr != "/") {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val uniqueValueEntity =
            db1TemplateLogicalDeleteUniqueDataRepository.findByUniqueValueAndRowDeleteDateStr(
                inputVo.uniqueValue,
                "/"
            )

        if (uniqueValueEntity != null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }


        oldEntity.uniqueValue = inputVo.uniqueValue

        val result = db1TemplateLogicalDeleteUniqueDataRepository.save(oldEntity)

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api20UpdateUniqueTestTableRowSampleOutputVo(
            result.uid!!,
            result.uniqueValue,
            result.rowCreateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api21DeleteUniqueTestTableRowSample(httpServletResponse: HttpServletResponse, index: Long) {
        val entity = db1TemplateLogicalDeleteUniqueDataRepository.findByUidAndRowDeleteDateStr(index, "/")

        if (entity == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        entity.rowDeleteDateStr =
            LocalDateTime.now().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        db1TemplateLogicalDeleteUniqueDataRepository.save(entity)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api22InsertFkParentRowSample(
        httpServletResponse: HttpServletResponse,
        inputVo: C7Service1TkV1DatabaseTestController.Api22InsertFkParentRowSampleInputVo
    ): C7Service1TkV1DatabaseTestController.Api22InsertFkParentRowSampleOutputVo? {
        val result = db1TemplateFkTestParentRepository.save(
            Db1_Template_FkTestParent(
                inputVo.fkParentName
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api22InsertFkParentRowSampleOutputVo(
            result.uid!!,
            result.parentName,
            result.rowCreateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api23InsertFkChildRowSample(
        httpServletResponse: HttpServletResponse,
        parentUid: Long,
        inputVo: C7Service1TkV1DatabaseTestController.Api23InsertFkChildRowSampleInputVo
    ): C7Service1TkV1DatabaseTestController.Api23InsertFkChildRowSampleOutputVo? {
        val parentEntityOpt = db1TemplateFkTestParentRepository.findById(parentUid)

        if (parentEntityOpt.isEmpty) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val parentEntity = parentEntityOpt.get()

        val result = db1TemplateFkTestManyToOneChildRepository.save(
            Db1_Template_FkTestManyToOneChild(
                inputVo.fkChildName,
                parentEntity
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api23InsertFkChildRowSampleOutputVo(
            result.uid!!,
            result.childName,
            result.fkTestParent.parentName,
            result.rowCreateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    ////
    override fun api24SelectFkTestTableRowsSample(httpServletResponse: HttpServletResponse): C7Service1TkV1DatabaseTestController.Api24SelectFkTestTableRowsSampleOutputVo? {
        val resultEntityList =
            db1TemplateFkTestParentRepository.findAllByRowDeleteDateStrOrderByRowCreateDate("/")

        val entityVoList =
            ArrayList<C7Service1TkV1DatabaseTestController.Api24SelectFkTestTableRowsSampleOutputVo.ParentEntityVo>()
        for (resultEntity in resultEntityList) {
            val childEntityVoList: ArrayList<C7Service1TkV1DatabaseTestController.Api24SelectFkTestTableRowsSampleOutputVo.ParentEntityVo.ChildEntityVo> =
                arrayListOf()

            val childList =
                db1TemplateFkTestManyToOneChildRepository.findAllByFkTestParentAndRowDeleteDateStrOrderByRowCreateDate(
                    resultEntity,
                    "/"
                )

            for (childEntity in childList) {
                childEntityVoList.add(
                    C7Service1TkV1DatabaseTestController.Api24SelectFkTestTableRowsSampleOutputVo.ParentEntityVo.ChildEntityVo(
                        childEntity.uid!!,
                        childEntity.childName,
                        childEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                        childEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                    )
                )
            }

            entityVoList.add(
                C7Service1TkV1DatabaseTestController.Api24SelectFkTestTableRowsSampleOutputVo.ParentEntityVo(
                    resultEntity.uid!!,
                    resultEntity.parentName,
                    resultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    childEntityVoList
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api24SelectFkTestTableRowsSampleOutputVo(
            entityVoList
        )
    }


    ////
    override fun api24Dot1SelectFkTestTableRowsByNativeQuerySample(httpServletResponse: HttpServletResponse): C7Service1TkV1DatabaseTestController.Api24SelectFkTestTableRowsByNativeQuerySampleDot1OutputVo? {
        val resultEntityList = db1NativeRepository.forC7N24Dot1()

        val entityVoList =
            ArrayList<C7Service1TkV1DatabaseTestController.Api24SelectFkTestTableRowsByNativeQuerySampleDot1OutputVo.ChildEntityVo>()
        for (resultEntity in resultEntityList) {
            entityVoList.add(
                C7Service1TkV1DatabaseTestController.Api24SelectFkTestTableRowsByNativeQuerySampleDot1OutputVo.ChildEntityVo(
                    resultEntity.childUid,
                    resultEntity.childName,
                    resultEntity.childCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.childUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.parentUid,
                    resultEntity.parentName
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api24SelectFkTestTableRowsByNativeQuerySampleDot1OutputVo(
            entityVoList
        )
    }


    ////
    override fun api25GetNativeQueryReturnValueTest(
        httpServletResponse: HttpServletResponse,
        inputVal: Boolean
    ): C7Service1TkV1DatabaseTestController.Api25GetNativeQueryReturnValueTestOutputVo? {
        // boolean 값을 갖고오기 위한 테스트 테이블이 존재하지 않는다면 하나 생성하기
        val justBooleanEntity = db1TemplateJustBooleanTestRepository.findAll()
        if (justBooleanEntity.isEmpty()) {
            db1TemplateJustBooleanTestRepository.save(
                Db1_Template_JustBooleanTest(
                    true
                )
            )
        }

        val resultEntity = db1NativeRepository.forC7N25(inputVal)

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api25GetNativeQueryReturnValueTestOutputVo(
            // 쿼리문 내에서 True, False 로 반환하는 값은 Long 타입으로 받습니다.
            resultEntity.normalBoolValue == 1L,
            resultEntity.funcBoolValue == 1L,
            resultEntity.ifBoolValue == 1L,
            resultEntity.caseBoolValue == 1L,

            // 테이블 쿼리의 Boolean 값은 그대로 Boolean 타입으로 받습니다.
            resultEntity.tableColumnBoolValue
        )
    }


    ////
    override fun api26SqlInjectionTest(
        httpServletResponse: HttpServletResponse,
        searchKeyword: String
    ): C7Service1TkV1DatabaseTestController.Api26SqlInjectionTestOutputVo? {
        // jpaRepository : Injection Safe
        val jpaRepositoryResultEntityList =
            db1TemplateTestsRepository.findAllByContentOrderByRowCreateDate(
                searchKeyword
            )

        val jpaRepositoryResultList: ArrayList<C7Service1TkV1DatabaseTestController.Api26SqlInjectionTestOutputVo.TestEntityVo> =
            arrayListOf()
        for (jpaRepositoryResultEntity in jpaRepositoryResultEntityList) {
            jpaRepositoryResultList.add(
                C7Service1TkV1DatabaseTestController.Api26SqlInjectionTestOutputVo.TestEntityVo(
                    jpaRepositoryResultEntity.uid!!,
                    jpaRepositoryResultEntity.content,
                    jpaRepositoryResultEntity.randomNum,
                    jpaRepositoryResultEntity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    jpaRepositoryResultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    jpaRepositoryResultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                )
            )
        }

        // JPQL : Injection Safe
        val jpqlResultEntityList =
            db1TemplateTestsRepository.findAllByContentOrderByRowCreateDateJpql(
                searchKeyword
            )

        val jpqlResultList: ArrayList<C7Service1TkV1DatabaseTestController.Api26SqlInjectionTestOutputVo.TestEntityVo> =
            arrayListOf()
        for (jpqlEntity in jpqlResultEntityList) {
            jpqlResultList.add(
                C7Service1TkV1DatabaseTestController.Api26SqlInjectionTestOutputVo.TestEntityVo(
                    jpqlEntity.uid!!,
                    jpqlEntity.content,
                    jpqlEntity.randomNum,
                    jpqlEntity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    jpqlEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    jpqlEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                )
            )
        }

        // NativeQuery : Injection Safe
        val nativeQueryResultEntityList =
            db1NativeRepository.forC7N26(
                searchKeyword
            )

        val nativeQueryResultList: ArrayList<C7Service1TkV1DatabaseTestController.Api26SqlInjectionTestOutputVo.TestEntityVo> =
            arrayListOf()
        for (nativeQueryEntity in nativeQueryResultEntityList) {
            nativeQueryResultList.add(
                C7Service1TkV1DatabaseTestController.Api26SqlInjectionTestOutputVo.TestEntityVo(
                    nativeQueryEntity.uid,
                    nativeQueryEntity.content,
                    nativeQueryEntity.randomNum,
                    nativeQueryEntity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    nativeQueryEntity.rowCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    nativeQueryEntity.rowUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                )
            )
        }

        /*
            결론 : 위 세 방식은 모두 SQL Injection 공격에서 안전합니다.
                셋 모두 쿼리문에 직접 값을 입력하는 것이 아니며, 매개변수로 먼저 받아서 JPA 를 경유하여 입력되므로,
                라이브러리가 자동으로 인젝션 공격을 막아주게 됩니다.
         */

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api26SqlInjectionTestOutputVo(
            jpaRepositoryResultList,
            jpqlResultList,
            nativeQueryResultList
        )
    }


    ////
    override fun api27SelectFkTableRowsWithLatestChildSample(httpServletResponse: HttpServletResponse): C7Service1TkV1DatabaseTestController.Api27SelectFkTableRowsWithLatestChildSampleOutputVo? {
        val resultEntityList = db1NativeRepository.forC7N27()

        val entityVoList =
            ArrayList<C7Service1TkV1DatabaseTestController.Api27SelectFkTableRowsWithLatestChildSampleOutputVo.ParentEntityVo>()
        for (resultEntity in resultEntityList) {
            entityVoList.add(
                C7Service1TkV1DatabaseTestController.Api27SelectFkTableRowsWithLatestChildSampleOutputVo.ParentEntityVo(
                    resultEntity.parentUid,
                    resultEntity.parentName,
                    resultEntity.parentCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.parentUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    if (resultEntity.childUid == null) {
                        null
                    } else {
                        C7Service1TkV1DatabaseTestController.Api27SelectFkTableRowsWithLatestChildSampleOutputVo.ParentEntityVo.ChildEntityVo(
                            resultEntity.childUid!!,
                            resultEntity.childName!!,
                            resultEntity.childCreateDate!!.atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                            resultEntity.childUpdateDate!!.atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                        )
                    }
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return C7Service1TkV1DatabaseTestController.Api27SelectFkTableRowsWithLatestChildSampleOutputVo(
            entityVoList
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api28DeleteFkChildRowSample(httpServletResponse: HttpServletResponse, index: Long) {
        val entityOpt = db1TemplateFkTestManyToOneChildRepository.findById(index)

        if (entityOpt.isEmpty) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        db1TemplateFkTestManyToOneChildRepository.deleteById(index)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api29DeleteFkParentRowSample(httpServletResponse: HttpServletResponse, index: Long) {
        val entityOpt = db1TemplateFkTestParentRepository.findById(index)

        if (entityOpt.isEmpty) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        db1TemplateFkTestParentRepository.deleteById(index)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api35FkTableTransactionTest(
        httpServletResponse: HttpServletResponse
    ) {
        val parentEntity = db1TemplateFkTestParentRepository.save(
            Db1_Template_FkTestParent(
                "transaction test"
            )
        )

        db1TemplateFkTestManyToOneChildRepository.save(
            Db1_Template_FkTestManyToOneChild(
                "transaction test1",
                parentEntity
            )
        )

        db1TemplateFkTestManyToOneChildRepository.save(
            Db1_Template_FkTestManyToOneChild(
                "transaction test2",
                parentEntity
            )
        )

        throw Exception("Transaction Rollback Test!")
    }


    ////
    override fun api36FkTableNonTransactionTest(httpServletResponse: HttpServletResponse) {
        val parentEntity = db1TemplateFkTestParentRepository.save(
            Db1_Template_FkTestParent(
                "transaction test"
            )
        )

        db1TemplateFkTestManyToOneChildRepository.save(
            Db1_Template_FkTestManyToOneChild(
                "transaction test1",
                parentEntity
            )
        )

        db1TemplateFkTestManyToOneChildRepository.save(
            Db1_Template_FkTestManyToOneChild(
                "transaction test2",
                parentEntity
            )
        )

        throw Exception("No Transaction Exception Test!")
    }
}
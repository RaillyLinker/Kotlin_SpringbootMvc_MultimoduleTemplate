package com.raillylinker.module_jpa.jpa_beans.db1_main.repositories

import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.Db1_Template_TestData
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

// 주의 : NativeRepository 의 반환값으로 기본 Entity 객체는 매핑되지 않으므로 OutputVo Interface 를 작성하여 사용할것.
// Output Interface 변수에 is 로 시작되는 변수는 매핑이 안되므로 사용하지 말것.

/* SQL Select 의 실행 순서
    1. FROM 절: 데이터베이스에서 데이터를 추출할 테이블이나 뷰를 지정합니다.
    2. JOIN 절: FROM 절에서 얻어온 테이블에 조건에 맞게 결합합니다. 여러 JOIN 절의 실행 순서는, 쿼리문 내 JOIN 작성 순서대로 진행됩니다.
    3. WHERE 절: FROM 절에서 지정된 테이블에서 필터링을 수행합니다. 조건에 맞지 않는 행을 제외합니다.
    4. GROUP BY 절: 그룹별로 데이터를 집계하기 위해 데이터를 그룹화합니다. GROUP BY 절에 지정된 열을 기준으로 행을 그룹화하고, 이후 집계 함수를 사용하여 각 그룹에 대한 집계를 계산합니다.
    5. HAVING 절: GROUP BY 절에서 그룹화된 결과에 대한 조건을 지정합니다. HAVING 절은 WHERE 절과 유사하지만, 그룹별로 조건을 적용하여 그룹을 필터링합니다.
    6. SELECT 절: 쿼리 결과 집합에 포함할 열을 선택합니다. GROUP BY 절과 함께 사용할 때는 집계 함수를 포함할 수 있습니다.
    7. ORDER BY 절: 결과 집합을 정렬합니다. ORDER BY 절은 SELECT 문이 실행된 후에 적용됩니다.
 */
@Repository
interface Db1_Native_Repository : JpaRepository<Db1_Template_TestData, Long> {
    // <C7>
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            test_data.uid AS uid, 
            test_data.row_create_date AS rowCreateDate, 
            test_data.row_update_date AS rowUpdateDate, 
            test_data.content AS content, 
            test_data.random_num AS randomNum, 
            test_data.test_datetime AS testDatetime, 
            ABS(test_data.random_num-:num) AS distance 
            FROM 
            template.test_data AS test_data 
            WHERE 
            test_data.row_delete_date_str = '/' 
            ORDER BY 
            distance
            """
    )
    fun forC7N5(
        @Param(value = "num") num: Int
    ): List<ForC7N5OutputVo>

    interface ForC7N5OutputVo {
        var uid: Long
        var rowCreateDate: LocalDateTime
        var rowUpdateDate: LocalDateTime
        var content: String
        var randomNum: Int
        var testDatetime: LocalDateTime
        var distance: Int
    }


    ////
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            test_data.uid AS uid, 
            test_data.content AS content, 
            test_data.random_num AS randomNum, 
            test_data.test_datetime AS testDatetime, 
            test_data.row_create_date AS rowCreateDate, 
            test_data.row_update_date AS rowUpdateDate, 
            ABS(TIMESTAMPDIFF(MICROSECOND, test_data.row_create_date, :date)) AS timeDiffMicroSec 
            FROM 
            template.test_data AS test_data 
            WHERE 
            test_data.row_delete_date_str = '/' 
            ORDER BY 
            timeDiffMicroSec
            """
    )
    fun forC7N6(
        @Param(value = "date") date: LocalDateTime
    ): List<ForC7N6OutputVo>

    interface ForC7N6OutputVo {
        var uid: Long
        var content: String
        var randomNum: Int
        var testDatetime: LocalDateTime
        var rowCreateDate: LocalDateTime
        var rowUpdateDate: LocalDateTime
        var timeDiffMicroSec: Long
    }


    ////
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            test_data.uid AS uid, 
            test_data.row_create_date AS rowCreateDate, 
            test_data.row_update_date AS rowUpdateDate, 
            test_data.content AS content, 
            test_data.random_num AS randomNum, 
            test_data.test_datetime AS testDatetime, 
            ABS(test_data.random_num-:num) AS distance 
            FROM 
            template.test_data AS test_data 
            WHERE 
            test_data.row_delete_date_str = '/' 
            ORDER BY 
            distance
            """,
        countQuery = """
            SELECT 
            COUNT(*) 
            FROM 
            template.test_data AS test_data 
            WHERE 
            test_data.row_delete_date_str = '/'
            """
    )
    fun forC7N8(
        @Param(value = "num") num: Int,
        pageable: Pageable
    ): Page<ForC7N8OutputVo>

    interface ForC7N8OutputVo {
        var uid: Long
        var rowCreateDate: LocalDateTime
        var rowUpdateDate: LocalDateTime
        var content: String
        var randomNum: Int
        var testDatetime: LocalDateTime
        var distance: Int
    }


    ////
    @Modifying // Native Query 에서 Delete, Update 문은 이것을 붙여야함
    @Query(
        nativeQuery = true,
        value = """
            UPDATE 
            template.test_data 
            SET 
            content = :content, 
            test_datetime = :testDatetime 
            WHERE 
            uid = :uid
            """
    )
    fun forC7N10(
        @Param(value = "uid") uid: Long,
        @Param(value = "content") content: String,
        @Param(value = "testDatetime") testDatetime: LocalDateTime
    )


    ////
    // like 문을 사용할 때, replace 로 검색어와 탐색 정보의 공백을 없애줌으로써 공백에 유연한 검색이 가능
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            test_data.uid AS uid, 
            test_data.row_create_date AS rowCreateDate, 
            test_data.row_update_date AS rowUpdateDate, 
            test_data.content AS content, 
            test_data.random_num AS randomNum, 
            test_data.test_datetime AS testDatetime 
            FROM 
            template.test_data AS test_data 
            WHERE 
            REPLACE(test_data.content, ' ', '') LIKE REPLACE(CONCAT('%',:searchKeyword,'%'), ' ', '') AND 
            test_data.row_delete_date_str = '/' 
            ORDER BY 
            test_data.row_create_date DESC
            """,
        countQuery = """
            SELECT 
            COUNT(*) 
            FROM 
            template.test_data AS test_data 
            WHERE 
            REPLACE(test_data.content, ' ', '') LIKE REPLACE(CONCAT('%',:searchKeyword,'%'), ' ', '') AND 
            test_data.row_delete_date_str = '/'
            """
    )
    fun forC7N11(
        @Param(value = "searchKeyword") searchKeyword: String,
        pageable: Pageable
    ): Page<ForC7N11OutputVo>

    interface ForC7N11OutputVo {
        var uid: Long
        var rowCreateDate: LocalDateTime
        var rowUpdateDate: LocalDateTime
        var content: String
        var randomNum: Int
        var testDatetime: LocalDateTime
    }


    ////
    /*
        [중복 없는 페이징 구현 쿼리 설명]
        - 아래 방법은, 논리적 삭제를 사용하는 테이블에만 사용이 가능하며,
            행 자체가 삭제되어 행 번호가 변경될 가능성이 존재하는 테이블에는 사용할 수 없습니다.

        - FROM 절에 서브쿼리를 사용하는 복잡한 쿼리입니다.
            데이터베이스 종류에 따라 오동작을 할 가능성이 있으며,
            성능 문제를 야기할 수 있습니다.

        (From 외곽 부분)
        1. 먼저 가져올 정보를 인터페이스에 맞게 가져옵니다.
            주의할 점은, where 문을 절대 사용하지 않으며, order by 로 순서만 정렬해야 합니다.
            그리고, 여기서 순서를 정하므로, 바깥 부분에서는 ORDER BY 를 사용하면 안됩니다.
            이것이 orderedCoreTable 입니다. (순서 정렬)

        2. orderedCoreTable 에 rowNum 을 붙여줍니다.
            이것이 rowNumTable 입니다. (행 번호 붙이기)

        - 여기까지 하여, 정렬 기준에 따라 행이 붙여진 rowNumTable 가 생성됩니다.
            이곳에 여러 필터링을 하면 되는데,
            먼저 중복 방지 기준이 되는 아이템(클라이언트에서 받은 마지막 아이템)의 고유값을 사용하여 그 행번호를 찾아야 합니다.

        3. rowNumTable 에서 where 문을 사용하여, 원하는 uid 의 rowNum 을 찾고,
            이보다 큰 rowNum 을 추려냅니다. (앵커의 행 번호 검색 후, 그것을 포함하여 앞의 결과를 제거)

        4. 3번을 했다면, 이제부터 where 문과 limit 을 사용하여 결과를 마음껏 필터링 하면 됩니다. (본격적인 필터링)
     */
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            * 
            FROM 
            (
                SELECT 
                *, 
                @rownum \:= @rownum + 1 AS rowNum 
                FROM 
                (
                    SELECT 
                    test_data.uid AS uid, 
                    test_data.row_create_date AS rowCreateDate, 
                    test_data.row_update_date AS rowUpdateDate, 
                    test_data.content as content, 
                    test_data.random_num AS randomNum, 
                    test_data.test_datetime AS testDatetime, 
                    test_data.row_delete_date_str AS rowDeleteDateStr 
                    FROM 
                    template.test_data AS test_data 
                    ORDER BY 
                    test_data.row_create_date DESC
                ) AS orderedCoreTable,
                (SELECT @rownum \:= 0) AS rowNumStart
            ) AS rowNumTable 
            WHERE 
            (
                :lastItemUid IS NULL OR 
                rowNumTable.rowNum > 
                (
                    SELECT 
                    rowNumCopy 
                    FROM 
                    (
                        SELECT 
                        *, 
                        @rownumCopy \:= @rownumCopy + 1 AS rowNumCopy 
                        FROM 
                        (
                            SELECT 
                            test_data.uid AS uid, 
                            test_data.row_create_date AS rowCreateDate, 
                            test_data.row_update_date AS rowUpdateDate, 
                            test_data.content as content, 
                            test_data.random_num AS randomNum, 
                            test_data.test_datetime AS testDatetime, 
                            test_data.row_delete_date_str AS rowDeleteDateStr 
                            FROM 
                            template.test_data AS test_data 
                            ORDER BY 
                            test_data.row_create_date DESC
                        ) AS orderedCoreTableCopy, 
                        (SELECT @rownumCopy \:= 0) AS rowNumStartCopy
                    ) AS rowNumTableCopy 
                    WHERE 
                    uid = :lastItemUid
                )
            ) AND 
            rowNumTable.rowDeleteDateStr = '/' 
            LIMIT :pageElementsCount
            """
    )
    fun forC7N14(
        @Param(value = "lastItemUid") lastItemUid: Long?,
        @Param(value = "pageElementsCount") pageElementsCount: Int
    ): List<ForC7N14OutputVo>

    interface ForC7N14OutputVo {
        var uid: Long
        var rowCreateDate: LocalDateTime
        var rowUpdateDate: LocalDateTime
        var content: String
        var randomNum: Int
        var testDatetime: LocalDateTime
        var rowDeleteDateStr: String
    }


    ////
    /*
        forC7N14 에 대해서 전체 카운팅 쿼리를 따로 만들어야 합니다.
        forC7N14 에서 From 문에서 사용하는 서브 쿼리를 제거하고,
        lastItemUid 비교 부분을 제거하며,
        LIMIT 을 제거하면 됩니다.
     */
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            COUNT(*) 
            FROM 
            template.test_data AS test_data 
            WHERE 
            test_data.row_delete_date_str = '/'
            """
    )
    fun forC7N14I1(): Long


    ////
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            COUNT(*) 
            FROM 
            template.test_data AS test_data 
            WHERE 
            test_data.row_delete_date_str = '/'
            """
    )
    fun forC7N16(): Long


    ////
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            test_data.uid AS uid, 
            test_data.content AS content, 
            test_data.random_num AS randomNum, 
            test_data.test_datetime AS testDatetime, 
            test_data.row_create_date AS rowCreateDate, 
            test_data.row_update_date AS rowUpdateDate 
            FROM 
            template.test_data AS test_data 
            WHERE 
            test_data.row_delete_date_str = '/' AND 
            test_data.uid = :testTableUid
            """
    )
    fun forC7N17(
        @Param(value = "testTableUid") testTableUid: Long
    ): ForC7N17OutputVo?

    interface ForC7N17OutputVo {
        var uid: Long
        var content: String
        var randomNum: Int
        var testDatetime: LocalDateTime
        var rowCreateDate: LocalDateTime
        var rowUpdateDate: LocalDateTime
    }


    ////
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            fk_test_many_to_one_child.uid AS childUid, 
            fk_test_many_to_one_child.child_name AS childName, 
            fk_test_many_to_one_child.row_create_date AS childCreateDate, 
            fk_test_many_to_one_child.row_update_date AS childUpdateDate, 
            fk_test_parent.uid AS parentUid, 
            fk_test_parent.parent_name AS parentName 
            FROM 
            template.fk_test_many_to_one_child AS fk_test_many_to_one_child 
            INNER JOIN 
            template.fk_test_parent AS fk_test_parent 
            ON 
            fk_test_parent.uid = fk_test_many_to_one_child.fk_test_parent_uid AND 
            fk_test_parent.row_delete_date_str = '/' 
            WHERE 
            fk_test_many_to_one_child.row_delete_date_str = '/' 
            """
    )
    fun forC7N24Dot1(): List<ForC7N24Dot1OutputVo>

    interface ForC7N24Dot1OutputVo {
        var childUid: Long
        var childName: String
        var childCreateDate: LocalDateTime
        var childUpdateDate: LocalDateTime
        var parentUid: Long
        var parentName: String
    }


    ////
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            true AS normalBoolValue, 
            (TRUE = :inputVal) AS funcBoolValue, 
            IF
            (
                (TRUE = :inputVal), 
                TRUE, 
                FALSE
            ) AS ifBoolValue, 
            (
                CASE 
                    WHEN 
                        (TRUE = :inputVal) 
                    THEN 
                        TRUE 
                    ELSE 
                        FALSE 
                END
            ) AS caseBoolValue, 
            (
                SELECT 
                bool_value 
                FROM 
                template.just_boolean_test 
                WHERE 
                uid = 1
            ) AS tableColumnBoolValue
            """
    )
    fun forC7N25(
        @Param(value = "inputVal") inputVal: Boolean
    ): ForC7N25OutputVo

    interface ForC7N25OutputVo {
        var normalBoolValue: Long
        var funcBoolValue: Long
        var ifBoolValue: Long
        var caseBoolValue: Long
        var tableColumnBoolValue: Boolean
    }


    ////
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            test_data.uid AS uid, 
            test_data.row_create_date AS rowCreateDate, 
            test_data.row_update_date AS rowUpdateDate, 
            test_data.content AS content, 
            test_data.random_num AS randomNum, 
            test_data.test_datetime AS testDatetime 
            FROM 
            template.test_data AS test_data 
            WHERE 
            test_data.content = :content 
            ORDER BY 
            test_data.row_create_date DESC
            """
    )
    fun forC7N26(
        @Param(value = "content") content: String
    ): List<ForC7N26OutputVo>

    interface ForC7N26OutputVo {
        var uid: Long
        var rowCreateDate: LocalDateTime
        var rowUpdateDate: LocalDateTime
        var content: String
        var randomNum: Int
        var testDatetime: LocalDateTime
    }


    ////
    /*
        [가장 최근의 자식 테이블만 Join 쿼리 설명]
        1. 가장 외곽의 From 부분은 부모 테이블을 이용하여 정상적으로 필터링을 하면 됩니다.

        2. 자식 테이블을 Join 하는 부분은 Inner 이든 Left 든 상관 없습니다.
            가장 외곽의 Join 문의 결과는 가장 최근에 등록한 자식 테이블의 결과값이 될 것입니다.
            즉, Join 문 내부에서 그러한 처리를 마친 단 하나의 자식 테이블 결과만이 반환될 것이기에,
            ON 문에서는 외래키 일치 여부만 확인하면 됩니다.

        3. 외곽 Join 문 안을 봅시다.
            여기서는 가장 최근의 데이터 1개를 가져와야 합니다.
            서브 쿼리에서 자식 테이블을 Select 하고, 동일한 자식 테이블에서 외래키 별로 그룹지어서 그중에서 가장 최신의 데이터를 구한 후,
            동일 외래키에서 가장 최근인 데이터를 Inner Join 하여 가져오면 됩니다.
            여기서, row_delete_date_stf 필터링을 하는 것은, 지워진 데이터는 제외하고 가장 최근의 데이터를 가져온다는 것으로,
            논리적 삭제가 아니라 실제 삭제를 생각해본다면 이것을 제외하는 것이 이해가 될 것입니다.

        - 주의사항 :
            JOIN 절에 서브쿼리를 사용하는 복잡한 쿼리입니다.
            데이터베이스 종류에 따라 오동작을 할 가능성이 있으며,
            성능 문제를 야기할 수 있으므로, 동작 테스트, 부하 테스트를 거친 후 사용을 고려하세요.

            가장 좋은 것은, 아래와 같이 서브쿼리를 사용하는 것이 아니라,
            fk_test_many_to_one_child 가 등록될 때마다 이를 데이터베이스 안에 저장 / 갱신 하도록 하여,
            이 데이터를 사용하는 것이 가장 깔끔합니다. (코드와 쿼리문은 깔끔하고 단순할수록 좋다고 강조드립니다.)
     */
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            fk_test_parent.uid AS parentUid, 
            fk_test_parent.parent_name AS parentName, 
            fk_test_parent.row_create_date AS parentCreateDate, 
            fk_test_parent.row_update_date AS parentUpdateDate, 
            fk_test_many_to_one_child.uid AS childUid, 
            fk_test_many_to_one_child.child_name AS childName, 
            fk_test_many_to_one_child.row_create_date AS childCreateDate, 
            fk_test_many_to_one_child.row_update_date AS childUpdateDate 
            FROM 
            template.fk_test_parent AS fk_test_parent 
            LEFT JOIN 
            (
                SELECT 
                fk_test_many_to_one_child1.* 
                FROM 
                template.fk_test_many_to_one_child AS fk_test_many_to_one_child1 
                INNER JOIN 
                (
                    SELECT 
                    MAX(fk_test_many_to_one_child2.row_create_date) AS max_row_create_date, 
                    fk_test_many_to_one_child2.fk_test_parent_uid, 
                    fk_test_many_to_one_child2.row_delete_date_str 
                    FROM 
                    template.fk_test_many_to_one_child AS fk_test_many_to_one_child2 
                    WHERE 
                    fk_test_many_to_one_child2.row_delete_date_str = '/' 
                    GROUP BY 
                    fk_test_many_to_one_child2.fk_test_parent_uid
                ) AS latest_fk_test_many_to_one_child 
                ON 
                latest_fk_test_many_to_one_child.fk_test_parent_uid = fk_test_many_to_one_child1.fk_test_parent_uid AND 
                latest_fk_test_many_to_one_child.max_row_create_date = fk_test_many_to_one_child1.row_create_date AND 
                latest_fk_test_many_to_one_child.row_delete_date_str = '/' 
                WHERE 
                fk_test_many_to_one_child1.row_delete_date_str = '/'
            ) AS fk_test_many_to_one_child 
            ON 
            fk_test_many_to_one_child.fk_test_parent_uid = fk_test_parent.uid AND 
            fk_test_many_to_one_child.row_delete_date_str = '/' 
            WHERE 
            fk_test_parent.row_delete_date_str = '/'
            """
    )
    fun forC7N27(): List<ForC7N27OutputVo>

    interface ForC7N27OutputVo {
        var parentUid: Long
        var parentName: String
        var parentCreateDate: LocalDateTime
        var parentUpdateDate: LocalDateTime

        var childUid: Long?
        var childName: String?
        var childCreateDate: LocalDateTime?
        var childUpdateDate: LocalDateTime?
    }


    //------------------------------------------------------------------------------------------------------------------
    // <C9>
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            *, 
            (
                6371 * acos(
                    cos(radians(latitude)) * 
                    cos(radians(:latitude)) * 
                    cos(radians(:longitude) - 
                    radians(longitude)) + 
                    sin(radians(latitude)) * 
                    sin(radians(:latitude))
                )
            ) AS distanceKiloMeter 
            FROM 
            template.test_map 
            WHERE 
            row_delete_date_str = '/' 
            HAVING 
            distanceKiloMeter <= :radiusKiloMeter 
            ORDER BY 
            distanceKiloMeter
            """
    )
    fun forC9N5(
        @Param(value = "latitude") latitude: Double,
        @Param(value = "longitude") longitude: Double,
        @Param(value = "radiusKiloMeter") radiusKiloMeter: Double
    ): List<ForC9N5OutputVo>

    interface ForC9N5OutputVo {
        var uid: Long
        var latitude: Double
        var longitude: Double
        var distanceKiloMeter: Double
    }

    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            * 
            FROM 
            template.test_map 
            WHERE 
            row_delete_date_str = '/' AND 
            latitude BETWEEN :southLatitude AND :northLatitude 
            AND 
            (
                (
                    :westLongitude <= :eastLongitude AND 
                    longitude BETWEEN :westLongitude AND :eastLongitude
                )
                OR
                (
                    :westLongitude > :eastLongitude AND 
                    (
                        longitude >= :westLongitude OR 
                        longitude <= :eastLongitude
                    )
                )
            )
            """
    )
    fun forC9N6(
        @Param(value = "northLatitude") northLatitude: Double,
        @Param(value = "eastLongitude") eastLongitude: Double,
        @Param(value = "southLatitude") southLatitude: Double,
        @Param(value = "westLongitude") westLongitude: Double
    ): List<ForC9N6OutputVo>

    interface ForC9N6OutputVo {
        var uid: Long
        var latitude: Double
        var longitude: Double
    }


    //------------------------------------------------------------------------------------------------------------------
    // <C10>
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            total_auth_member_lock_history.uid AS uid, 
            total_auth_member_lock_history.lock_reason_code AS lockReasonCode, 
            total_auth_member_lock_history.lock_reason AS lockReason, 
            total_auth_member_lock_history.lock_start AS lockStart, 
            total_auth_member_lock_history.lock_before AS lockBefore, 
            total_auth_member_lock_history.early_release AS earlyRelease, 
            total_auth_member_lock_history.row_create_date AS rowCreateDate, 
            total_auth_member_lock_history.row_update_date AS rowUpdateDate 
            FROM 
            railly_linker_company.total_auth_member_lock_history AS total_auth_member_lock_history 
            WHERE 
            total_auth_member_lock_history.row_delete_date_str = '/' AND 
            total_auth_member_lock_history.total_auth_member_uid = :totalAuthMemberUid AND 
            (
                total_auth_member_lock_history.early_release IS NULL OR 
                total_auth_member_lock_history.early_release > :currentTime 
            ) AND 
            (
                total_auth_member_lock_history.lock_before IS NULL OR 
                total_auth_member_lock_history.lock_before > :currentTime 
            ) AND 
            total_auth_member_lock_history.lock_start <= :currentTime 
            ORDER BY 
            total_auth_member_lock_history.row_create_date DESC
            """
    )
    fun findAllNowActivateMemberLockInfo(
        @Param(value = "totalAuthMemberUid") totalAuthMemberUid: Long,
        @Param("currentTime") currentTime: LocalDateTime
    ): List<FindAllNowActivateMemberLockInfoOutputVo>

    interface FindAllNowActivateMemberLockInfoOutputVo {
        var uid: Long
        var lockReasonCode: Byte
        var lockReason: String
        var lockStart: LocalDateTime
        var lockBefore: LocalDateTime?
        var earlyRelease: LocalDateTime
        var rowCreateDate: LocalDateTime
        var rowUpdateDate: LocalDateTime
    }
}
package com.raillylinker.module_common.util_components

import java.io.FileOutputStream
import java.io.InputStream

// [Excel 파일 처리 유틸]
interface ExcelFileUtil {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (액셀 파일을 읽어서 데이터 반환)
    // 파일 내 모든 시트, 모든 행열 데이터 반환
    // 반환값 : [시트번호][행번호][컬럼번호] == 셀값
    fun readExcel(
        excelFile: InputStream
    ): Map<String, List<List<String>>>

    // 시트, 행열 제한
    // 반환값 : [행번호][컬럼번호] == 셀값, 없는 시트번호라면 null 반환
    fun readExcel(
        excelFile: InputStream,
        sheetIdx: Int, // 가져올 시트 인덱스 (0부터 시작)
        rowRangeStartIdx: Int, // 가져올 행 범위 시작 인덱스 (0부터 시작)
        rowRangeEndIdx: Int?, // 가져올 행 범위 끝 인덱스 null 이라면 전부 (0부터 시작)
        columnRangeIdxList: List<Int>?, // 가져올 열 범위 인덱스 리스트 null 이라면 전부 (0부터 시작)
        minColumnLength: Int? // 결과 컬럼의 최소 길이 (길이를 넘으면 그대로, 미만이라면 "" 로 채움)
    ): List<List<String>>?


    // (액셀 파일생성)
    // inputExcelSheetDataMap : [시트이름][행번호][컬럼번호] == 셀값
    fun writeExcel(
        fileOutputStream: FileOutputStream,
        inputExcelSheetDataMap: Map<String, List<List<String>>>
    )


    // ---------------------------------------------------------------------------------------------
    // <비공개 메소드 공간>


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
}
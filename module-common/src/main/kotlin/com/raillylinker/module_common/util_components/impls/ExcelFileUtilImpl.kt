package com.raillylinker.module_common.util_components.impls

import com.raillylinker.module_common.util_components.ExcelFileUtil
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.binary.XSSFBSheetHandler
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable
import org.apache.poi.xssf.eventusermodel.XSSFReader
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler
import org.apache.poi.xssf.usermodel.XSSFComment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import org.xml.sax.ContentHandler
import org.xml.sax.InputSource
import java.io.FileOutputStream
import java.io.InputStream
import javax.xml.parsers.SAXParserFactory

// [Excel 파일 처리 유틸]
@Component
class ExcelFileUtilImpl : ExcelFileUtil {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (액셀 파일을 읽어서 데이터 반환)
    // 파일 내 모든 시트, 모든 행열 데이터 반환
    // 반환값 : [시트번호][행번호][컬럼번호] == 셀값
    override fun readExcel(
        excelFile: InputStream
    ): Map<String, List<List<String>>> {
        val resultObject: HashMap<String, List<List<String>>> = hashMapOf()

        OPCPackage.open(excelFile).use { opc ->
            val xssfReader = XSSFReader(opc)
            val sheets = xssfReader.sheetsData as XSSFReader.SheetIterator
            val styles = xssfReader.stylesTable
            val strings = ReadOnlySharedStringsTable(opc)

            while (sheets.hasNext()) {
                val sheetHandler = ExcelSheetHandler(0, null, null, null)

                sheets.next().use { sheet ->
                    val inputSource = InputSource(sheet)
                    val handle: ContentHandler = XSSFSheetXMLHandler(styles, strings, sheetHandler, false)
                    val saxParserFactory = SAXParserFactory.newInstance()
                    saxParserFactory.isNamespaceAware = true
                    val parser = saxParserFactory.newSAXParser()
                    val xmlReader = parser.xmlReader
                    xmlReader.contentHandler = handle
                    xmlReader.parse(inputSource)
                }

                resultObject[sheets.sheetName] = sheetHandler.sheet
            }
        }

        return resultObject
    }

    // 시트, 행열 제한
    // 반환값 : [행번호][컬럼번호] == 셀값, 없는 시트번호라면 null 반환
    override fun readExcel(
        excelFile: InputStream,
        sheetIdx: Int, // 가져올 시트 인덱스 (0부터 시작)
        rowRangeStartIdx: Int, // 가져올 행 범위 시작 인덱스 (0부터 시작)
        rowRangeEndIdx: Int?, // 가져올 행 범위 끝 인덱스 null 이라면 전부 (0부터 시작)
        columnRangeIdxList: List<Int>?, // 가져올 열 범위 인덱스 리스트 null 이라면 전부 (0부터 시작)
        minColumnLength: Int? // 결과 컬럼의 최소 길이 (길이를 넘으면 그대로, 미만이라면 "" 로 채움)
    ): List<List<String>>? {
        var resultObject: List<List<String>>? = null

        OPCPackage.open(excelFile).use { opc ->
            val xssfReader = XSSFReader(opc)
            val sheets = xssfReader.sheetsData as XSSFReader.SheetIterator
            val styles = xssfReader.stylesTable
            val strings = ReadOnlySharedStringsTable(opc)

            var currentSheetIdx = 0
            while (sheets.hasNext()) {
                sheets.next().use { sheet ->
                    if (sheetIdx == currentSheetIdx++) {
                        val sheetHandler =
                            ExcelSheetHandler(
                                rowRangeStartIdx,
                                rowRangeEndIdx,
                                columnRangeIdxList,
                                minColumnLength
                            )
                        val inputSource = InputSource(sheet)
                        val handle: ContentHandler = XSSFSheetXMLHandler(styles, strings, sheetHandler, false)
                        val saxParserFactory = SAXParserFactory.newInstance()
                        saxParserFactory.isNamespaceAware = true
                        val parser = saxParserFactory.newSAXParser()
                        val xmlReader = parser.xmlReader
                        xmlReader.contentHandler = handle
                        xmlReader.parse(inputSource)
                        resultObject = (sheetHandler.sheet)
                    }
                }
            }
        }

        return resultObject
    }


    // (액셀 파일생성)
    // inputExcelSheetDataMap : [시트이름][행번호][컬럼번호] == 셀값
    override fun writeExcel(
        fileOutputStream: FileOutputStream,
        inputExcelSheetDataMap: Map<String, List<List<String>>>
    ) {
        // 액셀 객체
        val workbook = XSSFWorkbook()

        for (inputExcelSheetData in inputExcelSheetDataMap) {
            val sheetName = inputExcelSheetData.key
            val rowList = inputExcelSheetData.value

            // 액셀 Sheet 객체
            val xssfSheet = workbook.createSheet(sheetName)

            for (rowIv in rowList.withIndex()) {
                // 액셀 Row 객체
                val curRow = xssfSheet.createRow(rowIv.index)

                for (colIv in rowIv.value.withIndex()) {
                    // 액셀 Cell 객체
                    curRow.createCell(colIv.index).setCellValue(colIv.value) // row 에 cell 저장
                }
            }
        }

        // 완성된 액셀 객체로 fos 쓰기
        workbook.write(fileOutputStream)
    }


    // ---------------------------------------------------------------------------------------------
    // <비공개 메소드 공간>


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    class ExcelSheetHandler(
        private val rowRangeStartIdx: Int, // 가져올 행 범위 시작 인덱스 (0부터 시작)
        private val rowRangeEndIdx: Int?, // 가져올 행 범위 끝 인덱스 null 이라면 전부 (0부터 시작)
        private val columnRangeIdxList: List<Int>?, // 가져올 열 범위 인덱스 리스트 null 이라면 전부 (0부터 시작)
        private val minColumnLength: Int? // 결과 컬럼의 최소 길이 (길이를 넘으면 그대로, 미만이라면 "" 로 채움)
    ) : XSSFBSheetHandler.SheetContentsHandler {
        // 한 Sheet 에 속한 모든 Row 에 속한 모든 Cell 의 데이터 ([행번호][컬럼번호] == 셀값)
        val sheet: ArrayList<List<String>> = ArrayList()

        // 한 Row 에 속한 모든 Cell 의 데이터 리스트 ([컬럼번호] == 셀값)
        private val row: ArrayList<String> = ArrayList()

        private var currentRowIdx = -1 // -1 : 선택 안됨
        private var currentColumnIdx = -1 // -1 : 선택 안됨

        private var nowOutRowRange = true

        init {
            if (rowRangeStartIdx < 0) { // 인덱스 번호가 0 보다 작음
                throw RuntimeException("rowRangeStartIdx 가 0 보다 작습니다.")
            }
            if (rowRangeEndIdx != null && rowRangeEndIdx < 0) { // 인덱스 번호가 0 보다 작음
                throw RuntimeException("rowRangeEndIdx 가 0 보다 작습니다.")
            }
            if (rowRangeEndIdx != null && (rowRangeStartIdx > rowRangeEndIdx)) {
                // start 인덱스가 end 인덱스 번호보다 큼
                throw RuntimeException("rowRangeStartIdx 가 rowRangeEndIdx 보다 큽니다.")
            }
        }

        // (각 Row 를 읽기 시작할 때 해당 Row 의 인덱스(0부터 시작)를 가져옴)
        override fun startRow(rowNum: Int) {
            currentRowIdx = rowNum // 현재 행 번호 저장
            currentColumnIdx = -1 // 현재 컬럼 번호 초기화

            // 행 스킵 기준 체크
            nowOutRowRange = rowRangeStartIdx > currentRowIdx ||
                    (rowRangeEndIdx != null && rowRangeEndIdx < currentRowIdx)
        }

        // (startRow 와 endRow 사이에 각 컬럼을 순회하며 실행됨)
        override fun cell(
            cellName: String, // 셀 이름 (0번 행에서 A1, 1번 행에서 A2 등등)
            cellValue: String, // 셀 값
            var3: XSSFComment?
        ) {
            if (nowOutRowRange) { // row 스킵
                return
            }

            // 현재 컬럼 번호 (0부터 시작되며, 앞의 cell 이 비어있다면 해당 인덱스들은 생략된 상태)
            val columnIdx = CellReference(cellName).col.toInt()

            val nowAddRowIdx = currentColumnIdx + 1 // 지금 추가되어야 할 인덱스 번호 (마지막 인덱스 +1)
            currentColumnIdx = columnIdx


            for (idx in nowAddRowIdx until columnIdx) {
                if (columnRangeIdxList == null || columnRangeIdxList.contains(idx)) {
                    row.add("")
                }
            }

            if (columnRangeIdxList == null || columnRangeIdxList.contains(columnIdx)) {
                row.add(cellValue)
            }
        }

        // (각 Row 읽기가 끝났을 때 해당 Row 의 인덱스(0부터 시작)를 가져옴)
        override fun endRow(rowNum: Int) {
            if (!nowOutRowRange) { // row 스킵
                if (minColumnLength != null) {
                    val columnLength = row.size
                    if (columnLength < minColumnLength) {
                        val lengthDiff = minColumnLength - columnLength

                        for (idx in 0 until lengthDiff) {
                            row.add("")
                        }
                    }
                }

                sheet.add(ArrayList(row))
                row.clear()
            }
        }

        override fun headerFooter(text: String, isHeader: Boolean, tagName: String) {
        }

        override fun hyperlinkCell(arg0: String, arg1: String, arg2: String, arg3: String, arg4: XSSFComment) {
        }
    }
}
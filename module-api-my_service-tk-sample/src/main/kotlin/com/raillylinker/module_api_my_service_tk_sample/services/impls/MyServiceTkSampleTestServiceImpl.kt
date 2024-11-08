package com.raillylinker.module_api_my_service_tk_sample.services.impls

import com.raillylinker.module_api_my_service_tk_sample.controllers.MyServiceTkSampleTestController
import com.raillylinker.module_api_my_service_tk_sample.services.MyServiceTkSampleTestService
import com.raillylinker.module_common.util_components.*
import com.raillylinker.module_kafka.kafka_components.producers.Kafka1MainProducer
import jakarta.servlet.http.HttpServletResponse
import org.apache.fontbox.ttf.TTFParser
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class MyServiceTkSampleTestServiceImpl(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,

    private val excelFileUtil: ExcelFileUtil,

    private val customUtil: CustomUtil,

    // 암복호화 유틸
    private val cryptoUtil: CryptoUtil,

    private val pdfGenerator: PdfGenerator,

    // 이메일 발송 유틸
    private val emailSender: EmailSender,
    // 네이버 메시지 발송 유틸
    private val naverSmsSenderComponent: NaverSmsSenderComponent,

    private val kafka1MainProducer: Kafka1MainProducer,

    private var serverProperties: ServerProperties,
    private val resourceLoader: ResourceLoader
) : MyServiceTkSampleTestService {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    override fun sendEmailTest(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleTestController.SendEmailTestInputVo
    ) {
        emailSender.sendMessageMail(
            inputVo.senderName,
            inputVo.receiverEmailAddressList.toTypedArray(),
            inputVo.carbonCopyEmailAddressList?.toTypedArray(),
            inputVo.subject,
            inputVo.message,
            null,
            inputVo.multipartFileList
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun sendHtmlEmailTest(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleTestController.SendHtmlEmailTestInputVo
    ) {
        // CID 는 첨부파일을 보내는 것과 동일한 의미입니다.
        // 고로 전송시 서버 성능에 악영향을 끼칠 가능성이 크고, CID 처리도 번거로우므로, CDN 을 사용하고, CID 는 되도록 사용하지 마세요.
        emailSender.sendThymeLeafHtmlMail(
            inputVo.senderName,
            inputVo.receiverEmailAddressList.toTypedArray(),
            inputVo.carbonCopyEmailAddressList?.toTypedArray(),
            inputVo.subject,
            "send_html_email_test/html_email_sample",
            hashMapOf(
                Pair("message", inputVo.message)
            ),
            null,
            hashMapOf(
                "html_email_sample_css" to ClassPathResource("static/send_html_email_test/html_email_sample.css"),
                "image_sample" to ClassPathResource("static/send_html_email_test/image_sample.jpg")
            ),
            null,
            inputVo.multipartFileList
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun naverSmsSample(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleTestController.NaverSmsSampleInputVo
    ) {
        val phoneNumberSplit = inputVo.phoneNumber.split(")") // ["82", "010-0000-0000"]

        // 국가 코드 (ex : 82)
        val countryCode = phoneNumberSplit[0]

        // 전화번호 (ex : "01000000000")
        val phoneNumber = (phoneNumberSplit[1].replace("-", "")).replace(" ", "")

        // SMS 전송
        val sendSmsResult = naverSmsSenderComponent.sendSms(
            NaverSmsSenderComponent.SendSmsInputVo(
                "SMS",
                countryCode,
                phoneNumber,
                inputVo.smsMessage
            )
        )

        if (!sendSmsResult) {
            throw Exception()
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun naverAlimTalkSample(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleTestController.NaverAlimTalkSampleInputVo
    ) {
        val phoneNumberSplit = inputVo.phoneNumber.split(")") // ["82", "010-0000-0000"]

        // 국가 코드 (ex : 82)
        val countryCode = phoneNumberSplit[0]

        // 전화번호 (ex : "01000000000")
        val phoneNumber = (phoneNumberSplit[1].replace("-", "")).replace(" ", "")

        // SMS 전송
        naverSmsSenderComponent.sendAlimTalk(
            NaverSmsSenderComponent.SendAlimTalkInputVo(
                inputVo.plusFriendId,
                inputVo.templateCode,
                arrayListOf(
                    NaverSmsSenderComponent.SendAlimTalkInputVo.MessageVo(
                        countryCode,
                        phoneNumber,
                        null,
                        inputVo.message,
                        null,
                        null,
                        null,
                        null,
                        true,
                        NaverSmsSenderComponent.SendAlimTalkInputVo.MessageVo.FailOverConfigVo(
                            null,
                            null,
                            null,
                            "카카오 실패시의 SMS 발송 메시지입니다."
                        )
                    )
                )
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun readExcelFileSample(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleTestController.ReadExcelFileSampleInputVo
    ): MyServiceTkSampleTestController.ReadExcelFileSampleOutputVo? {
        val excelData: List<List<String>>?
        inputVo.excelFile.inputStream.use { fileInputStream ->
            excelData = excelFileUtil.readExcel(
                fileInputStream,
                inputVo.sheetIdx,
                inputVo.rowRangeStartIdx,
                inputVo.rowRangeEndIdx,
                inputVo.columnRangeIdxList,
                inputVo.minColumnLength
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkSampleTestController.ReadExcelFileSampleOutputVo(
            excelData?.size ?: 0,
            excelData.toString()
        )
    }


    ////
    override fun writeExcelFileSample(httpServletResponse: HttpServletResponse) {
        // 파일 저장 디렉토리 경로
        val saveDirectoryPathString = "./by_product_files/test"
        val saveDirectoryPath = Paths.get(saveDirectoryPathString).toAbsolutePath().normalize()
        // 파일 저장 디렉토리 생성
        Files.createDirectories(saveDirectoryPath)

        // 확장자 포함 파일명 생성
        val saveFileName = "temp_${
            LocalDateTime.now().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        }.xlsx"

        // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
        val fileTargetPath = saveDirectoryPath.resolve(saveFileName).normalize()
        val file = fileTargetPath.toFile()

        val inputExcelSheetDataMap: HashMap<String, List<List<String>>> = hashMapOf()
        inputExcelSheetDataMap["testSheet1"] = listOf(
            listOf("1-1", "1-2", "1-3"),
            listOf("2-1", "2-2", "2-3"),
            listOf("3-1", "3-2", "3-3")
        )
        inputExcelSheetDataMap["testSheet2"] = listOf(
            listOf("1-1", "1-2"),
            listOf("2-1", "2-2")
        )

        file.outputStream().use { fileOutputStream ->
            excelFileUtil.writeExcel(fileOutputStream, inputExcelSheetDataMap)
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun htmlToPdfSample(
        httpServletResponse: HttpServletResponse
    ): ResponseEntity<Resource>? {
        // thymeLeaf 엔진으로 파싱한 HTML String 가져오기
        // 여기서 가져온 HTML 내에 기입된 static resources 의 경로는 절대경로가 아님
        val htmlString = customUtil.parseHtmlFileToHtmlString(
            "html_to_pdf_sample/html_to_pdf_sample", // thymeLeaf Html 이름 (ModelAndView 의 사용과 동일)
            // thymeLeaf 에 전해줄 데이터 Map
            mapOf(
                "title" to "PDF 변환 테스트"
            )
        )
        val savedFontFileNameMap: HashMap<String, String> = hashMapOf()
        val savedImgFilePathMap: HashMap<String, String> = hashMapOf()

        // htmlString 을 PDF 로 변환하여 저장
        // XHTML 1.0(strict), CSS 2.1 (@page 의 size 는 가능)
        savedFontFileNameMap["NanumGothicFile.ttf"] =
            "http://127.0.0.1:${serverProperties.port}/html_to_pdf_sample/NanumGothic.ttf"

        savedFontFileNameMap["NanumMyeongjo.ttf"] =
            "http://127.0.0.1:${serverProperties.port}/html_to_pdf_sample/NanumMyeongjo.ttf"

        savedImgFilePathMap["html_to_pdf_sample.jpg"] =
            resourceLoader.getResource("classpath:static/html_to_pdf_sample/html_to_pdf_sample.jpg").file.absolutePath

        val pdfByteArray = pdfGenerator.createPdfByteArrayFromHtmlString(
            htmlString,
            savedFontFileNameMap,
            savedImgFilePathMap
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return ResponseEntity<Resource>(
            InputStreamResource(pdfByteArray.inputStream()),
            HttpHeaders().apply {
                this.contentDisposition = ContentDisposition.builder("attachment")
                    .filename(
                        "result(${
                            LocalDateTime.now().atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                        }).pdf", StandardCharsets.UTF_8
                    )
                    .build()
                this.add(HttpHeaders.CONTENT_TYPE, "application/pdf")
            },
            HttpStatus.OK
        )
    }


    ////
    override fun multipartHtmlToPdfSample(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleTestController.MultipartHtmlToPdfSampleInputVo,
        controllerBasicMapping: String?
    ): ResponseEntity<Resource>? {
        val savedFontFileNameMap: HashMap<String, String> = hashMapOf()
        val savedImgFileList: ArrayList<File> = arrayListOf()
        val savedImgFilePathMap: HashMap<String, String> = hashMapOf()

        // htmlString 을 PDF 로 변환하여 저장
        // XHTML 1.0(strict), CSS 2.1 (@page 의 size 는 가능)
        try {
            if (inputVo.fontFiles != null) {
                for (fontFile in inputVo.fontFiles) {
                    // 파일 저장 기본 디렉토리 경로
                    val saveDirectoryPath: Path =
                        Paths.get("./by_product_files/uploads/fonts").toAbsolutePath().normalize()

                    // 파일 저장 기본 디렉토리 생성
                    Files.createDirectories(saveDirectoryPath)

                    // 원본 파일명(with suffix)
                    val multiPartFileNameString = StringUtils.cleanPath(fontFile.originalFilename!!)

                    // 파일 확장자 구분 위치
                    val fileExtensionSplitIdx = multiPartFileNameString.lastIndexOf('.')

                    // 확장자가 없는 파일명
                    val fileNameWithOutExtension: String
                    // 확장자
                    val fileExtension: String
                    // 폰트 이름
                    val ttfName: String

                    if (fileExtensionSplitIdx == -1) {
                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                        httpServletResponse.setHeader("api-result-code", "1")
                        return null
                    } else {
                        fileNameWithOutExtension = multiPartFileNameString.substring(0, fileExtensionSplitIdx)
                        fileExtension =
                            multiPartFileNameString.substring(fileExtensionSplitIdx + 1, multiPartFileNameString.length)
                        if (fileExtension != "ttf") {
                            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                            httpServletResponse.setHeader("api-result-code", "1")
                            return null
                        }

                        fontFile.inputStream.use { fontInputStream ->
                            TTFParser().parseEmbedded(fontInputStream).use { ttf ->
                                ttfName = ttf.name
                            }
                        }

                        val fontFileUrl =
                            "http://127.0.0.1:${serverProperties.port}${controllerBasicMapping ?: ""}/by_product_files/uploads/fonts/$ttfName.$fileExtension"

                        savedFontFileNameMap["$fileNameWithOutExtension.$fileExtension"] = fontFileUrl
                    }

                    val fontFilePath = saveDirectoryPath.resolve("$ttfName.$fileExtension").normalize()

                    if (!Files.exists(fontFilePath)) {
                        // multipartFile 을 targetPath 에 저장
                        fontFile.transferTo(fontFilePath)
                    }
                }
            }

            if (inputVo.imgFiles != null) {
                for (imgFile in inputVo.imgFiles) {
                    val multiPartFileNameString = StringUtils.cleanPath(imgFile.originalFilename!!)
                    val fileExtensionSplitIdx = multiPartFileNameString.lastIndexOf('.')

                    // 확장자가 없는 파일명
                    // 확장자
                    val fileExtension: String = if (fileExtensionSplitIdx == -1) {
                        ""
                    } else {
                        multiPartFileNameString.substring(fileExtensionSplitIdx + 1, multiPartFileNameString.length)
                    }
                    val tempFile: File = Files.createTempFile(null, ".$fileExtension").toFile()
                    imgFile.transferTo(tempFile)

                    savedImgFileList.add(tempFile)
                    savedImgFilePathMap[multiPartFileNameString] = tempFile.toString()
                }
            }

            val pdfByteArray = pdfGenerator.createPdfByteArrayFromHtmlString(
                String(inputVo.htmlFile.bytes, Charsets.UTF_8),
                savedFontFileNameMap,
                savedImgFilePathMap
            )

            httpServletResponse.status = HttpStatus.OK.value()
            return ResponseEntity<Resource>(
                InputStreamResource(pdfByteArray.inputStream()),
                HttpHeaders().apply {
                    this.contentDisposition = ContentDisposition.builder("attachment")
                        .filename(
                            "result(${
                                LocalDateTime.now().atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                            }).pdf", StandardCharsets.UTF_8
                        )
                        .build()
                    this.add(HttpHeaders.CONTENT_TYPE, "application/pdf")
                },
                HttpStatus.OK
            )
        } finally {
            for (imgFile in savedImgFileList) {
                val result = imgFile.delete()
                println("delete $imgFile : $result")
            }
        }
    }

    ////
    override fun downloadFontFile(
        httpServletResponse: HttpServletResponse,
        fileName: String
    ): ResponseEntity<Resource>? {
        // 프로젝트 루트 경로 (프로젝트 settings.gradle 이 있는 경로)
        val projectRootAbsolutePathString: String = File("").absolutePath

        // 파일 절대 경로 및 파일명 (프로젝트 루트 경로에 있는 by_product_files/test 폴더를 기준으로 함)
        val serverFilePathObject =
            Paths.get("$projectRootAbsolutePathString/by_product_files/uploads/fonts/$fileName")

        when {
            Files.isDirectory(serverFilePathObject) -> {
                // 파일이 디렉토리일때
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "1")
                return null
            }

            Files.notExists(serverFilePathObject) -> {
                // 파일이 없을 때
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "1")
                return null
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return ResponseEntity<Resource>(
            InputStreamResource(Files.newInputStream(serverFilePathObject)),
            HttpHeaders().apply {
                this.contentDisposition = ContentDisposition.builder("attachment")
                    .filename(fileName, StandardCharsets.UTF_8)
                    .build()
                this.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(serverFilePathObject))
            },
            HttpStatus.OK
        )
    }


    ////
    override fun sendKafkaTopicMessageTest(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleTestController.SendKafkaTopicMessageTestInputVo
    ) {
        // kafkaProducer1 에 토픽 메세지 발행
        kafka1MainProducer.sendMessageToTestTopic1(
            Kafka1MainProducer.SendMessageToTestTopic1InputVo(
                inputVo.message,
                1
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun processBuilderTest(
        httpServletResponse: HttpServletResponse,
        javaEnvironmentPath: String?
    ): MyServiceTkSampleTestController.ProcessBuilderTestOutputVo? {
        val javaEnv = javaEnvironmentPath ?: "java"

        // JAR 파일 실행 명령어 설정
        val javaJarPb = ProcessBuilder(javaEnv, "-jar", "./external_files/files_for_api_test/JarExample/Counter.jar")
        javaJarPb.directory(File(".")) // 현재 작업 디렉토리 설정

        // 프로세스 시작
        val javaJarProcess = javaJarPb.start()

        // 프로세스의 출력 스트림 가져오기
        val result: Long
        javaJarProcess.inputStream.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Read the result from the JAR execution
                result = reader.readLine()?.toLong() ?: 0

                // 프로세스 종료 대기
                val exitCode = javaJarProcess.waitFor()
                println("Exit Code: $exitCode")
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkSampleTestController.ProcessBuilderTestOutputVo(
            result
        )
    }


    ////
    override fun checkFontFileInnerName(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleTestController.CheckFontFileInnerNameInputVo
    ): MyServiceTkSampleTestController.CheckFontFileInnerNameOutputVo? {
        // MultipartFile에서 InputStream을 얻어옴
        val fontName: String
        inputVo.fontFile.inputStream.use { fontInputStream ->
            val parser = TTFParser()
            parser.parseEmbedded(fontInputStream).use { ttf ->
                fontName = ttf.name
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkSampleTestController.CheckFontFileInnerNameOutputVo(
            fontName
        )
    }


    ////
    override fun aes256EncryptTest(
        httpServletResponse: HttpServletResponse,
        plainText: String,
        alg: MyServiceTkSampleTestController.Aes256EncryptTestCryptoAlgEnum,
        initializationVector: String,
        encryptionKey: String
    ): MyServiceTkSampleTestController.Aes256EncryptTestOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkSampleTestController.Aes256EncryptTestOutputVo(
            cryptoUtil.encryptAES256(
                plainText,
                alg.alg,
                initializationVector,
                encryptionKey
            )
        )
    }


    ////
    override fun aes256DecryptTest(
        httpServletResponse: HttpServletResponse,
        encryptedText: String,
        alg: MyServiceTkSampleTestController.Aes256DecryptTestCryptoAlgEnum,
        initializationVector: String,
        encryptionKey: String
    ): MyServiceTkSampleTestController.Aes256DecryptTestOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkSampleTestController.Aes256DecryptTestOutputVo(
            cryptoUtil.decryptAES256(
                encryptedText,
                alg.alg,
                initializationVector,
                encryptionKey
            )
        )
    }


    ////
    override fun jsoupTest(httpServletResponse: HttpServletResponse, fix: Boolean): String? {
        val htmlString =
            """
                <!DOCTYPE HTML>
                <head>
                    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type"/>
                    <meta charset='UTF-8'/>
                    <title>Test</title>
                </head>
                <body>
                <div>
                    <div>
                        <div class="fix">
                            수정 전
                        </div>
                    </div>

                    <div>
                        <div class="fix">
                            <h1>수정 전</h1>
                        </div>
                    </div>
                </div>

                </body>
                </html>
            """.trimIndent()

        if (fix) {
            val doc = Jsoup.parse(htmlString)
            // 클래스가 "fix"인 모든 요소를 선택합니다.
            val buyerSignElements = doc.select(".fix")
            for (buyerSignElement in buyerSignElements) {
                // 선택된 요소 내의 모든 자식 요소를 삭제합니다.
                buyerSignElement.children().remove()
                buyerSignElement.text("")
                // 선택된 요소에 태그를 추가합니다.
                buyerSignElement.append("<span>수정 완료</span>")
            }

            httpServletResponse.status = HttpStatus.OK.value()
            return doc.html()
        } else {
            httpServletResponse.status = HttpStatus.OK.value()
            return htmlString
        }
    }
}
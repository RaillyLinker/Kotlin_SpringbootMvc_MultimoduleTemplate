package com.raillylinker.module_api_my_service_tk_sample.services.impls

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.raillylinker.module_api_my_service_tk_sample.controllers.MyServiceTkSampleRequestTestController
import com.raillylinker.module_api_my_service_tk_sample.services.MyServiceTkSampleRequestTestService
import com.raillylinker.module_common.classes.SseEmitterWrapper
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.util.FileCopyUtils
import org.springframework.util.StringUtils
import org.springframework.web.context.request.async.DeferredResult
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Service
class MyServiceTkSampleRequestTestServiceImpl(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String
) : MyServiceTkSampleRequestTestService {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)

    // (스레드 풀)
    private val executorService: ExecutorService = Executors.newCachedThreadPool()


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    override fun basicRequestTest(httpServletResponse: HttpServletResponse): String? {
        httpServletResponse.status = HttpStatus.OK.value()
        return activeProfile
    }


    ////
    override fun redirectTest(httpServletResponse: HttpServletResponse): ModelAndView? {
        val mv = ModelAndView()
        mv.viewName = "redirect:/my-service/tk/sample/request-test"

        return mv
    }


    ////
    override fun forwardTest(httpServletResponse: HttpServletResponse): ModelAndView? {
        val mv = ModelAndView()
        mv.viewName = "forward:/my-service/tk/sample/request-test"

        return mv
    }


    ////
    override fun getRequestTest(
        httpServletResponse: HttpServletResponse,
        queryParamString: String,
        queryParamStringNullable: String?,
        queryParamInt: Int,
        queryParamIntNullable: Int?,
        queryParamDouble: Double,
        queryParamDoubleNullable: Double?,
        queryParamBoolean: Boolean,
        queryParamBooleanNullable: Boolean?,
        queryParamStringList: List<String>,
        queryParamStringListNullable: List<String>?
    ): MyServiceTkSampleRequestTestController.GetRequestTestOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkSampleRequestTestController.GetRequestTestOutputVo(
            queryParamString,
            queryParamStringNullable,
            queryParamInt,
            queryParamIntNullable,
            queryParamDouble,
            queryParamDoubleNullable,
            queryParamBoolean,
            queryParamBooleanNullable,
            queryParamStringList,
            queryParamStringListNullable
        )
    }


    ////
    override fun getRequestTestWithPathParam(
        httpServletResponse: HttpServletResponse,
        pathParamInt: Int
    ): MyServiceTkSampleRequestTestController.GetRequestTestWithPathParamOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkSampleRequestTestController.GetRequestTestWithPathParamOutputVo(pathParamInt)
    }


    ////
    override fun postRequestTestWithApplicationJsonTypeRequestBody(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleRequestTestController.PostRequestTestWithApplicationJsonTypeRequestBodyInputVo
    ): MyServiceTkSampleRequestTestController.PostRequestTestWithApplicationJsonTypeRequestBodyOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkSampleRequestTestController.PostRequestTestWithApplicationJsonTypeRequestBodyOutputVo(
            inputVo.requestBodyString,
            inputVo.requestBodyStringNullable,
            inputVo.requestBodyInt,
            inputVo.requestBodyIntNullable,
            inputVo.requestBodyDouble,
            inputVo.requestBodyDoubleNullable,
            inputVo.requestBodyBoolean,
            inputVo.requestBodyBooleanNullable,
            inputVo.requestBodyStringList,
            inputVo.requestBodyStringListNullable
        )
    }


    ////
    override fun postRequestTestWithApplicationJsonTypeRequestBody2(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleRequestTestController.PostRequestTestWithApplicationJsonTypeRequestBody2InputVo
    ): MyServiceTkSampleRequestTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo? {
        val objectList: MutableList<MyServiceTkSampleRequestTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo> =
            mutableListOf()

        for (objectVo in inputVo.objectVoList) {
            val subObjectVoList: MutableList<MyServiceTkSampleRequestTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo.SubObjectVo> =
                mutableListOf()
            for (subObject in objectVo.subObjectVoList) {
                subObjectVoList.add(
                    MyServiceTkSampleRequestTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo.SubObjectVo(
                        subObject.requestBodyString,
                        subObject.requestBodyStringList
                    )
                )
            }

            objectList.add(
                MyServiceTkSampleRequestTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo(
                    objectVo.requestBodyString,
                    objectVo.requestBodyStringList,
                    MyServiceTkSampleRequestTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo.SubObjectVo(
                        objectVo.subObjectVo.requestBodyString,
                        objectVo.subObjectVo.requestBodyStringList
                    ),
                    subObjectVoList
                )
            )
        }

        val subObjectVoList: MutableList<MyServiceTkSampleRequestTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo.SubObjectVo> =
            mutableListOf()
        for (subObject in inputVo.objectVo.subObjectVoList) {
            subObjectVoList.add(
                MyServiceTkSampleRequestTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo.SubObjectVo(
                    subObject.requestBodyString,
                    subObject.requestBodyStringList
                )
            )
        }

        val outputVo =
            MyServiceTkSampleRequestTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo(
                MyServiceTkSampleRequestTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo(
                    inputVo.objectVo.requestBodyString,
                    inputVo.objectVo.requestBodyStringList,
                    MyServiceTkSampleRequestTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo.SubObjectVo(
                        inputVo.objectVo.subObjectVo.requestBodyString,
                        inputVo.objectVo.subObjectVo.requestBodyStringList
                    ),
                    subObjectVoList
                ),
                objectList
            )

        httpServletResponse.status = HttpStatus.OK.value()
        return outputVo
    }


    ////
    override fun postRequestTestWithNoInputAndOutput(
        httpServletResponse: HttpServletResponse
    ) {
        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun postRequestTestWithFormTypeRequestBody(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleRequestTestController.PostRequestTestWithFormTypeRequestBodyInputVo
    ): MyServiceTkSampleRequestTestController.PostRequestTestWithFormTypeRequestBodyOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkSampleRequestTestController.PostRequestTestWithFormTypeRequestBodyOutputVo(
            inputVo.requestFormString,
            inputVo.requestFormStringNullable,
            inputVo.requestFormInt,
            inputVo.requestFormIntNullable,
            inputVo.requestFormDouble,
            inputVo.requestFormDoubleNullable,
            inputVo.requestFormBoolean,
            inputVo.requestFormBooleanNullable,
            inputVo.requestFormStringList,
            inputVo.requestFormStringListNullable
        )
    }


    ////
    override fun postRequestTestWithMultipartFormTypeRequestBody(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleRequestTestController.PostRequestTestWithMultipartFormTypeRequestBodyInputVo
    ): MyServiceTkSampleRequestTestController.PostRequestTestWithMultipartFormTypeRequestBodyOutputVo? {
        // 파일 저장 기본 디렉토리 경로
        val saveDirectoryPath: Path = Paths.get("./by_product_files/test").toAbsolutePath().normalize()

        // 파일 저장 기본 디렉토리 생성
        Files.createDirectories(saveDirectoryPath)

        // 원본 파일명(with suffix)
        val multiPartFileNameString = StringUtils.cleanPath(inputVo.multipartFile.originalFilename!!)

        // 파일 확장자 구분 위치
        val fileExtensionSplitIdx = multiPartFileNameString.lastIndexOf('.')

        // 확장자가 없는 파일명
        val fileNameWithOutExtension: String
        // 확장자
        val fileExtension: String

        if (fileExtensionSplitIdx == -1) {
            fileNameWithOutExtension = multiPartFileNameString
            fileExtension = ""
        } else {
            fileNameWithOutExtension = multiPartFileNameString.substring(0, fileExtensionSplitIdx)
            fileExtension =
                multiPartFileNameString.substring(fileExtensionSplitIdx + 1, multiPartFileNameString.length)
        }

        // multipartFile 을 targetPath 에 저장
        inputVo.multipartFile.transferTo(
            // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
            saveDirectoryPath.resolve(
                "${fileNameWithOutExtension}(${
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                }).$fileExtension"
            ).normalize()
        )

        if (inputVo.multipartFileNullable != null) {
            // 원본 파일명(with suffix)
            val multiPartFileNullableNameString =
                StringUtils.cleanPath(inputVo.multipartFileNullable.originalFilename!!)

            // 파일 확장자 구분 위치
            val nullableFileExtensionSplitIdx = multiPartFileNullableNameString.lastIndexOf('.')

            // 확장자가 없는 파일명
            val nullableFileNameWithOutExtension: String
            // 확장자
            val nullableFileExtension: String

            if (nullableFileExtensionSplitIdx == -1) {
                nullableFileNameWithOutExtension = multiPartFileNullableNameString
                nullableFileExtension = ""
            } else {
                nullableFileNameWithOutExtension =
                    multiPartFileNullableNameString.substring(0, nullableFileExtensionSplitIdx)
                nullableFileExtension =
                    multiPartFileNullableNameString.substring(
                        nullableFileExtensionSplitIdx + 1,
                        multiPartFileNullableNameString.length
                    )
            }

            // multipartFile 을 targetPath 에 저장
            inputVo.multipartFileNullable.transferTo(
                // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
                saveDirectoryPath.resolve(
                    "${nullableFileNameWithOutExtension}(${
                        LocalDateTime.now().atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                    }).$nullableFileExtension"
                ).normalize()
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkSampleRequestTestController.PostRequestTestWithMultipartFormTypeRequestBodyOutputVo(
            inputVo.requestFormString,
            inputVo.requestFormStringNullable,
            inputVo.requestFormInt,
            inputVo.requestFormIntNullable,
            inputVo.requestFormDouble,
            inputVo.requestFormDoubleNullable,
            inputVo.requestFormBoolean,
            inputVo.requestFormBooleanNullable,
            inputVo.requestFormStringList,
            inputVo.requestFormStringListNullable
        )
    }


    ////
    override fun postRequestTestWithMultipartFormTypeRequestBody2(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleRequestTestController.PostRequestTestWithMultipartFormTypeRequestBody2InputVo
    ): MyServiceTkSampleRequestTestController.PostRequestTestWithMultipartFormTypeRequestBody2OutputVo? {
        // 파일 저장 기본 디렉토리 경로
        val saveDirectoryPath: Path = Paths.get("./by_product_files/test").toAbsolutePath().normalize()

        // 파일 저장 기본 디렉토리 생성
        Files.createDirectories(saveDirectoryPath)

        for (multipartFile in inputVo.multipartFileList) {
            // 원본 파일명(with suffix)
            val multiPartFileNameString = StringUtils.cleanPath(multipartFile.originalFilename!!)

            // 파일 확장자 구분 위치
            val fileExtensionSplitIdx = multiPartFileNameString.lastIndexOf('.')

            // 확장자가 없는 파일명
            val fileNameWithOutExtension: String
            // 확장자
            val fileExtension: String

            if (fileExtensionSplitIdx == -1) {
                fileNameWithOutExtension = multiPartFileNameString
                fileExtension = ""
            } else {
                fileNameWithOutExtension = multiPartFileNameString.substring(0, fileExtensionSplitIdx)
                fileExtension =
                    multiPartFileNameString.substring(fileExtensionSplitIdx + 1, multiPartFileNameString.length)
            }

            // multipartFile 을 targetPath 에 저장
            multipartFile.transferTo(
                // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
                saveDirectoryPath.resolve(
                    "${fileNameWithOutExtension}(${
                        LocalDateTime.now().atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                    }).$fileExtension"
                ).normalize()
            )
        }

        if (inputVo.multipartFileNullableList != null) {
            for (multipartFileNullable in inputVo.multipartFileNullableList) {
                // 원본 파일명(with suffix)
                val multiPartFileNullableNameString =
                    StringUtils.cleanPath(multipartFileNullable.originalFilename!!)

                // 파일 확장자 구분 위치
                val nullableFileExtensionSplitIdx = multiPartFileNullableNameString.lastIndexOf('.')

                // 확장자가 없는 파일명
                val nullableFileNameWithOutExtension: String
                // 확장자
                val nullableFileExtension: String

                if (nullableFileExtensionSplitIdx == -1) {
                    nullableFileNameWithOutExtension = multiPartFileNullableNameString
                    nullableFileExtension = ""
                } else {
                    nullableFileNameWithOutExtension =
                        multiPartFileNullableNameString.substring(0, nullableFileExtensionSplitIdx)
                    nullableFileExtension =
                        multiPartFileNullableNameString.substring(
                            nullableFileExtensionSplitIdx + 1,
                            multiPartFileNullableNameString.length
                        )
                }

                // multipartFile 을 targetPath 에 저장
                multipartFileNullable.transferTo(
                    // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
                    saveDirectoryPath.resolve(
                        "${nullableFileNameWithOutExtension}(${
                            LocalDateTime.now().atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                        }).$nullableFileExtension"
                    ).normalize()
                )
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkSampleRequestTestController.PostRequestTestWithMultipartFormTypeRequestBody2OutputVo(
            inputVo.requestFormString,
            inputVo.requestFormStringNullable,
            inputVo.requestFormInt,
            inputVo.requestFormIntNullable,
            inputVo.requestFormDouble,
            inputVo.requestFormDoubleNullable,
            inputVo.requestFormBoolean,
            inputVo.requestFormBooleanNullable,
            inputVo.requestFormStringList,
            inputVo.requestFormStringListNullable
        )
    }


    ////
    override fun postRequestTestWithMultipartFormTypeRequestBody3(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleRequestTestController.PostRequestTestWithMultipartFormTypeRequestBody3InputVo
    ): MyServiceTkSampleRequestTestController.PostRequestTestWithMultipartFormTypeRequestBody3OutputVo? {
        // input Json String to Object
        val inputJsonObject =
            Gson().fromJson<MyServiceTkSampleRequestTestController.PostRequestTestWithMultipartFormTypeRequestBody3InputVo.InputJsonObject>(
                inputVo.jsonString, // 해석하려는 json 형식의 String
                object :
                    TypeToken<MyServiceTkSampleRequestTestController.PostRequestTestWithMultipartFormTypeRequestBody3InputVo.InputJsonObject>() {}.type // 파싱할 데이터 객체 타입
            )

        // 파일 저장 기본 디렉토리 경로
        val saveDirectoryPath: Path = Paths.get("./by_product_files/test").toAbsolutePath().normalize()

        // 파일 저장 기본 디렉토리 생성
        Files.createDirectories(saveDirectoryPath)

        // 원본 파일명(with suffix)
        val multiPartFileNameString = StringUtils.cleanPath(inputVo.multipartFile.originalFilename!!)

        // 파일 확장자 구분 위치
        val fileExtensionSplitIdx = multiPartFileNameString.lastIndexOf('.')

        // 확장자가 없는 파일명
        val fileNameWithOutExtension: String
        // 확장자
        val fileExtension: String

        if (fileExtensionSplitIdx == -1) {
            fileNameWithOutExtension = multiPartFileNameString
            fileExtension = ""
        } else {
            fileNameWithOutExtension = multiPartFileNameString.substring(0, fileExtensionSplitIdx)
            fileExtension =
                multiPartFileNameString.substring(fileExtensionSplitIdx + 1, multiPartFileNameString.length)
        }

        // multipartFile 을 targetPath 에 저장
        inputVo.multipartFile.transferTo(
            // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
            saveDirectoryPath.resolve(
                "${fileNameWithOutExtension}(${
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                }).$fileExtension"
            ).normalize()
        )

        if (inputVo.multipartFileNullable != null) {
            // 원본 파일명(with suffix)
            val multiPartFileNullableNameString =
                StringUtils.cleanPath(inputVo.multipartFileNullable.originalFilename!!)

            // 파일 확장자 구분 위치
            val nullableFileExtensionSplitIdx = multiPartFileNullableNameString.lastIndexOf('.')

            // 확장자가 없는 파일명
            val nullableFileNameWithOutExtension: String
            // 확장자
            val nullableFileExtension: String

            if (nullableFileExtensionSplitIdx == -1) {
                nullableFileNameWithOutExtension = multiPartFileNullableNameString
                nullableFileExtension = ""
            } else {
                nullableFileNameWithOutExtension =
                    multiPartFileNullableNameString.substring(0, nullableFileExtensionSplitIdx)
                nullableFileExtension =
                    multiPartFileNullableNameString.substring(
                        nullableFileExtensionSplitIdx + 1,
                        multiPartFileNullableNameString.length
                    )
            }

            // multipartFile 을 targetPath 에 저장
            inputVo.multipartFileNullable.transferTo(
                // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
                saveDirectoryPath.resolve(
                    "${nullableFileNameWithOutExtension}(${
                        LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH_mm-ss-SSS")
                        )
                    }).$nullableFileExtension"
                ).normalize()
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkSampleRequestTestController.PostRequestTestWithMultipartFormTypeRequestBody3OutputVo(
            inputJsonObject.requestFormString,
            inputJsonObject.requestFormStringNullable,
            inputJsonObject.requestFormInt,
            inputJsonObject.requestFormIntNullable,
            inputJsonObject.requestFormDouble,
            inputJsonObject.requestFormDoubleNullable,
            inputJsonObject.requestFormBoolean,
            inputJsonObject.requestFormBooleanNullable,
            inputJsonObject.requestFormStringList,
            inputJsonObject.requestFormStringListNullable
        )
    }


    ////
    override fun generateErrorTest(httpServletResponse: HttpServletResponse) {
        throw RuntimeException("Test Error")
    }


    ////
    override fun returnResultCodeThroughHeaders(
        httpServletResponse: HttpServletResponse,
        errorType: MyServiceTkSampleRequestTestController.ReturnResultCodeThroughHeadersErrorTypeEnum?
    ) {
        if (errorType == null) {
            httpServletResponse.status = HttpStatus.OK.value()
        } else {
            when (errorType) {
                MyServiceTkSampleRequestTestController.ReturnResultCodeThroughHeadersErrorTypeEnum.A -> {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                }

                MyServiceTkSampleRequestTestController.ReturnResultCodeThroughHeadersErrorTypeEnum.B -> {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                }

                MyServiceTkSampleRequestTestController.ReturnResultCodeThroughHeadersErrorTypeEnum.C -> {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "3")
                }
            }
        }
    }


    ////
    override fun responseDelayTest(httpServletResponse: HttpServletResponse, delayTimeSec: Long) {
        val endTime = System.currentTimeMillis() + (delayTimeSec * 1000)

        while (System.currentTimeMillis() < endTime) {
            // 아무 것도 하지 않고 대기
            Thread.sleep(100)  // 100ms마다 스레드를 잠들게 하여 CPU 사용률을 줄임
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun returnTextStringTest(httpServletResponse: HttpServletResponse): String? {
        httpServletResponse.status = HttpStatus.OK.value()
        return "test Complete!"
    }


    ////
    override fun returnTextHtmlTest(httpServletResponse: HttpServletResponse): ModelAndView? {
        val modelAndView = ModelAndView()
        modelAndView.viewName = "return_text_html_test/html_response_example"

        httpServletResponse.status = HttpStatus.OK.value()
        return modelAndView
    }


    ////
    override fun returnByteDataTest(httpServletResponse: HttpServletResponse): Resource? {
        httpServletResponse.status = HttpStatus.OK.value()
        return ByteArrayResource(
            byteArrayOf(
                'a'.code.toByte(),
                'b'.code.toByte(),
                'c'.code.toByte(),
                'd'.code.toByte(),
                'e'.code.toByte(),
                'f'.code.toByte()
            )
        )
    }


    ////
    override fun videoStreamingTest(
        videoHeight: MyServiceTkSampleRequestTestController.VideoStreamingTestVideoHeight,
        httpServletResponse: HttpServletResponse
    ): Resource? {
        // 프로젝트 루트 경로 (프로젝트 settings.gradle 이 있는 경로)
        val projectRootAbsolutePathString: String = File("").absolutePath

        // 파일 절대 경로 및 파일명
        val serverFileAbsolutePathString =
            "$projectRootAbsolutePathString/module-api-my_service-tk-sample/src/main/resources/static/video_streaming_test"

        // 멤버십 등의 정보로 해상도 제한을 걸 수도 있음
        val serverFileNameString =
            when (videoHeight) {
                MyServiceTkSampleRequestTestController.VideoStreamingTestVideoHeight.H240 -> {
                    "test_240p.mp4"
                }

                MyServiceTkSampleRequestTestController.VideoStreamingTestVideoHeight.H360 -> {
                    "test_360p.mp4"
                }

                MyServiceTkSampleRequestTestController.VideoStreamingTestVideoHeight.H480 -> {
                    "test_480p.mp4"
                }

                MyServiceTkSampleRequestTestController.VideoStreamingTestVideoHeight.H720 -> {
                    "test_720p.mp4"
                }
            }

        // 반환값에 전해줄 FIS
        val fileByteArray: ByteArray
        FileInputStream("$serverFileAbsolutePathString/$serverFileNameString").use { fileInputStream ->
            fileByteArray = FileCopyUtils.copyToByteArray(fileInputStream)
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return ByteArrayResource(fileByteArray)
    }


    ////
    override fun audioStreamingTest(httpServletResponse: HttpServletResponse): Resource? {
        // 프로젝트 루트 경로 (프로젝트 settings.gradle 이 있는 경로)
        val projectRootAbsolutePathString: String = File("").absolutePath

        // 파일 절대 경로 및 파일명
        val serverFileAbsolutePathString =
            "$projectRootAbsolutePathString/module-api-my_service-tk-sample/src/main/resources/static/audio_streaming_test"
        val serverFileNameString = "test.mp3"

        // 반환값에 전해줄 FIS
        val fileByteArray: ByteArray
        FileInputStream("$serverFileAbsolutePathString/$serverFileNameString").use { fileInputStream ->
            fileByteArray = FileCopyUtils.copyToByteArray(fileInputStream)
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return ByteArrayResource(fileByteArray)
    }


    ////
    override fun asynchronousResponseTest(httpServletResponse: HttpServletResponse): DeferredResult<MyServiceTkSampleRequestTestController.AsynchronousResponseTestOutputVo>? {
        // 연결 타임아웃 밀리초
        val deferredResultTimeoutMs = 1000L * 60
        val deferredResult = DeferredResult<MyServiceTkSampleRequestTestController.AsynchronousResponseTestOutputVo>(
            deferredResultTimeoutMs
        )

        // 비동기 처리
        executorService.execute {
            // 지연시간 대기
            val delayMs = 5000L
            Thread.sleep(delayMs)

            // 결과 반환
            deferredResult.setResult(MyServiceTkSampleRequestTestController.AsynchronousResponseTestOutputVo("${delayMs / 1000} 초 경과 후 반환했습니다."))
        }

        // 결과 대기 객체를 먼저 반환
        httpServletResponse.status = HttpStatus.OK.value()
        return deferredResult
    }


    ////
    // api20 에서 발급한 Emitter 객체
    private val api20SseEmitterWrapperMbr = SseEmitterWrapper()
    override fun sseTestSubscribe(httpServletResponse: HttpServletResponse, lastSseEventId: String?): SseEmitter? {
        // 수신 객체
        val sseEmitter = api20SseEmitterWrapperMbr.getSseEmitter(null, lastSseEventId)

        httpServletResponse.status = HttpStatus.OK.value()
        return sseEmitter
    }


    ////
    private var api21TriggerTestCountMbr = 0
    override fun sseTestEventTrigger(httpServletResponse: HttpServletResponse) {
        // emitter 이벤트 전송
        val nowTriggerTestCount = ++api21TriggerTestCountMbr

        api20SseEmitterWrapperMbr.broadcastEvent(
            "triggerTest",
            "trigger $nowTriggerTestCount"
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun emptyListRequestTest(
        httpServletResponse: HttpServletResponse,
        stringList: List<String>,
        inputVo: MyServiceTkSampleRequestTestController.EmptyListRequestTestInputVo
    ): MyServiceTkSampleRequestTestController.EmptyListRequestTestOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkSampleRequestTestController.EmptyListRequestTestOutputVo(
            stringList,
            inputVo.requestBodyStringList
        )
    }
}
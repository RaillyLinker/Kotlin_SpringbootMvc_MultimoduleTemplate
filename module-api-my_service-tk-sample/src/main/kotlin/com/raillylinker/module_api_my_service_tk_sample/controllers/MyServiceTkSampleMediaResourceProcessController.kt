package com.raillylinker.module_api_my_service_tk_sample.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.module_api_my_service_tk_sample.services.MyServiceTkSampleMediaResourceProcessService
import com.raillylinker.module_common.util_components.ImageProcessUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Tag(
    name = "/my-service/tk/sample/media-resource-process APIs",
    description = "미디어 리소스(이미지, 비디오, 오디오 등...) 처리 API 컨트롤러"
)
@Controller
@RequestMapping("/my-service/tk/sample/media-resource-process")
class MyServiceTkSampleMediaResourceProcessController(
    private val service: MyServiceTkSampleMediaResourceProcessService
) {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
    @Operation(
        summary = "정적 이미지 파일(지원 타입은 description 에 후술)을 업로드 하여 리사이징 후 다운",
        description = "multipart File 로 받은 이미지 파일을 업로드 하여 리사이징 후 다운\n\n" +
                "지원 타입 : jpg, jpeg, bmp, png, gif(움직이지 않는 타입)\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.\n\n" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required\n\n" +
                                "1 : multipartImageFile 이 지원하는 타입의 이미지 파일이 아닙니다.\n\n",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @PostMapping(
        path = ["/resize-image"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    @ResponseBody
    fun resizeImage(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @ModelAttribute
        @RequestBody
        inputVo: ResizeImageInputVo
    ): ResponseEntity<Resource>? {
        return service.resizeImage(inputVo, httpServletResponse)
    }

    data class ResizeImageInputVo(
        @Schema(description = "업로드 이미지 파일", required = true)
        @JsonProperty("multipartImageFile")
        val multipartImageFile: MultipartFile,
        @Schema(description = "이미지 리사이징 너비", required = true, example = "300")
        @JsonProperty("resizingWidth")
        val resizingWidth: Int,
        @Schema(description = "이미지 리사이징 높이", required = true, example = "400")
        @JsonProperty("resizingHeight")
        val resizingHeight: Int,
        @Schema(description = "이미지 포멧", required = true, example = "BMP")
        @JsonProperty("imageType")
        val imageType: ImageProcessUtil.ResizeImageTypeEnum
    )


    ////
    @Operation(
        summary = "서버에 저장된 움직이는 Gif 이미지 파일에서 프레임을 PNG 이미지 파일로 분리한 후 by_product_files/test 폴더 안에 저장",
        description = "서버에 저장된 움직이는 Gif 이미지 파일에서 프레임을 PNG 이미지 파일로 분리한 후 by_product_files/test 폴더 안에 저장\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @PostMapping(
        path = ["/split-animated-gif"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun splitAnimatedGif(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.splitAnimatedGif(httpServletResponse)
    }


    ////
    @Operation(
        summary = "서버에 저장된 움직이는 PNG 이미지 프레임들을 움직이는 Gif 파일로 병합 후 by_product_files/test 폴더 안에 저장",
        description = "서버에 저장된 움직이는 PNG 이미지 프레임들을 움직이는 Gif 파일로 병합 후 by_product_files/test 폴더 안에 저장\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @PostMapping(
        path = ["/merge-images-to-animated-gif"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun mergeImagesToAnimatedGif(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.mergeImagesToAnimatedGif(httpServletResponse)
    }


    ////
    @Operation(
        summary = "동적 GIF 이미지 파일을 업로드 하여 리사이징 후 다운",
        description = "multipart File 로 받은 움직이는 GIF 이미지 파일을 업로드 하여 리사이징 후 다운\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.\n\n" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required\n\n" +
                                "1 : multipartImageFile 이 지원하는 타입의 이미지 파일이 아닙니다.\n\n",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @PostMapping(
        path = ["/resize-gif-image"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    @ResponseBody
    fun resizeGifImage(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @ModelAttribute
        @RequestBody
        inputVo: ResizeGifImageInputVo
    ): ResponseEntity<Resource>? {
        return service.resizeGifImage(inputVo, httpServletResponse)
    }

    data class ResizeGifImageInputVo(
        @Schema(description = "업로드 이미지 파일", required = true)
        @JsonProperty("multipartImageFile")
        val multipartImageFile: MultipartFile,
        @Schema(description = "이미지 리사이징 너비", required = true, example = "300")
        @JsonProperty("resizingWidth")
        val resizingWidth: Int,
        @Schema(description = "이미지 리사이징 높이", required = true, example = "400")
        @JsonProperty("resizingHeight")
        val resizingHeight: Int
    )


    ////
    @Operation(
        summary = "서명 생성 테스트",
        description = "입력받은 문자열을 투명 배경 서명 이미지로 만들어 by_product_files/test 폴더에 저장\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @PostMapping(
        path = ["/create-signature"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun createSignature(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @RequestBody
        inputVo: CreateSignatureInputVo
    ) {
        service.createSignature(httpServletResponse, inputVo)
    }

    data class CreateSignatureInputVo(
        @Schema(description = "서명 문자", required = true, example = "홍길동")
        @JsonProperty("signatureText")
        val signatureText: String
    )
}
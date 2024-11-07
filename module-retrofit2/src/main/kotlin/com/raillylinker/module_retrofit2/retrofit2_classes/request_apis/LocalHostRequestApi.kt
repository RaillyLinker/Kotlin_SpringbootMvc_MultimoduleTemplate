package com.raillylinker.module_retrofit2.retrofit2_classes.request_apis

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

// (한 주소에 대한 API 요청명세)
// 사용법은 아래 기본 사용 샘플을 참고하여 추상함수를 작성하여 사용
interface LocalHostRequestApi {
    // [기본 요청 테스트 API]
    // 이 API 를 요청하면 현재 실행중인 프로필 이름을 반환합니다.
    // (api-result-code)
    @GET("/service1/tk/v1/request-test")
    fun getService1TkV1RequestTest(): Call<String?>


    ////
    // [요청 Redirect 테스트 API]
    // 이 API 를 요청하면 /service1/tk/v1/request-test 로 Redirect 됩니다.
    // (api-result-code)
    @GET("/service1/tk/v1/request-test/redirect-to-blank")
    fun getService1TkV1RequestTestRedirectToBlank(): Call<String?>


    ////
    // [요청 Forward 테스트 API]
    // 이 API 를 요청하면 /service1/tk/v1/request-test 로 Forward 됩니다.
    // (api-result-code)
    @GET("/service1/tk/v1/request-test/forward-to-blank")
    fun getService1TkV1RequestTestForwardToBlank(): Call<String?>


    ////
    // [Get 요청(Query Parameter) 테스트 API]
    // Query Parameter 를 받는 Get 메소드 요청 테스트
    // (api-result-code)
    @GET("/service1/tk/v1/request-test/get-request")
    fun getService1TkV1RequestTestGetRequest(
        @Query("queryParamString") queryParamString: String,
        @Query("queryParamStringNullable") queryParamStringNullable: String?,
        @Query("queryParamInt") queryParamInt: Int,
        @Query("queryParamIntNullable") queryParamIntNullable: Int?,
        @Query("queryParamDouble") queryParamDouble: Double,
        @Query("queryParamDoubleNullable") queryParamDoubleNullable: Double?,
        @Query("queryParamBoolean") queryParamBoolean: Boolean,
        @Query("queryParamBooleanNullable") queryParamBooleanNullable: Boolean?,
        @Query("queryParamStringList") queryParamStringList: List<String>,
        @Query("queryParamStringListNullable") queryParamStringListNullable: List<String>?
    ): Call<GetService1TkV1RequestTestGetRequestOutputVO?>

    data class GetService1TkV1RequestTestGetRequestOutputVO(
        @SerializedName("queryParamString")
        @Expose
        val queryParamString: String,
        @SerializedName("queryParamStringNullable")
        @Expose
        val queryParamStringNullable: String?,
        @SerializedName("queryParamInt")
        @Expose
        val queryParamInt: Int,
        @SerializedName("queryParamIntNullable")
        @Expose
        val queryParamIntNullable: Int?,
        @SerializedName("queryParamDouble")
        @Expose
        val queryParamDouble: Double,
        @SerializedName("queryParamDoubleNullable")
        @Expose
        val queryParamDoubleNullable: Double?,
        @SerializedName("queryParamBoolean")
        @Expose
        val queryParamBoolean: Boolean,
        @SerializedName("queryParamBooleanNullable")
        @Expose
        val queryParamBooleanNullable: Boolean?,
        @SerializedName("queryParamStringList")
        @Expose
        val queryParamStringList: List<String>,
        @SerializedName("queryParamStringListNullable")
        @Expose
        val queryParamStringListNullable: List<String>?
    )


    ////
    // [Get 요청(Path Parameter) 테스트 API]
    // Path Parameter 를 받는 Get 메소드 요청 테스트
    // (api-result-code)
    @GET("/service1/tk/v1/request-test/get-request/{pathParamInt}")
    fun getService1TkV1RequestTestGetRequestPathParamInt(
        @Path("pathParamInt") pathParamInt: Int
    ): Call<GetService1TkV1RequestTestGetRequestPathParamIntOutputVO?>

    data class GetService1TkV1RequestTestGetRequestPathParamIntOutputVO(
        @SerializedName("pathParamInt")
        @Expose
        val pathParamInt: Int
    )


    ////
    // [Post 요청(Application-Json) 테스트 API]
    // application-json 형태의 Request Body 를 받는 Post 메소드 요청 테스트
    // (api-result-code)
    @POST("/service1/tk/v1/request-test/post-request-application-json")
    fun postService1TkV1RequestTestPostRequestApplicationJson(
        @Body inputVo: PostService1TkV1RequestTestPostRequestApplicationJsonInputVO
    ): Call<PostService1TkV1RequestTestPostRequestApplicationJsonOutputVO?>

    data class PostService1TkV1RequestTestPostRequestApplicationJsonInputVO(
        @SerializedName("requestBodyString")
        @Expose
        val requestBodyString: String,
        @SerializedName("requestBodyStringNullable")
        @Expose
        val requestBodyStringNullable: String?,
        @SerializedName("requestBodyInt")
        @Expose
        val requestBodyInt: Int,
        @SerializedName("requestBodyIntNullable")
        @Expose
        val requestBodyIntNullable: Int?,
        @SerializedName("requestBodyDouble")
        @Expose
        val requestBodyDouble: Double,
        @SerializedName("requestBodyDoubleNullable")
        @Expose
        val requestBodyDoubleNullable: Double?,
        @SerializedName("requestBodyBoolean")
        @Expose
        val requestBodyBoolean: Boolean,
        @SerializedName("requestBodyBooleanNullable")
        @Expose
        val requestBodyBooleanNullable: Boolean?,
        @SerializedName("requestBodyStringList")
        @Expose
        val requestBodyStringList: List<String>,
        @SerializedName("requestBodyStringListNullable")
        @Expose
        val requestBodyStringListNullable: List<String>?
    )

    data class PostService1TkV1RequestTestPostRequestApplicationJsonOutputVO(
        @SerializedName("requestBodyString")
        @Expose
        val requestBodyString: String,
        @SerializedName("requestBodyStringNullable")
        @Expose
        val requestBodyStringNullable: String?,
        @SerializedName("requestBodyInt")
        @Expose
        val requestBodyInt: Int,
        @SerializedName("requestBodyIntNullable")
        @Expose
        val requestBodyIntNullable: Int?,
        @SerializedName("requestBodyDouble")
        @Expose
        val requestBodyDouble: Double,
        @SerializedName("requestBodyDoubleNullable")
        @Expose
        val requestBodyDoubleNullable: Double?,
        @SerializedName("requestBodyBoolean")
        @Expose
        val requestBodyBoolean: Boolean,
        @SerializedName("requestBodyBooleanNullable")
        @Expose
        val requestBodyBooleanNullable: Boolean?,
        @SerializedName("requestBodyStringList")
        @Expose
        val requestBodyStringList: List<String>,
        @SerializedName("requestBodyStringListNullable")
        @Expose
        val requestBodyStringListNullable: List<String>?
    )


    ////
    // [Post 요청(x-www-form-urlencoded) 테스트 API]
    // x-www-form-urlencoded 형태의 Request Body 를 받는 Post 메소드 요청 테스트
    // (api-result-code)
    @POST("service1/tk/v1/request-test/post-request-x-www-form-urlencoded")
    @FormUrlEncoded
    fun postService1TkV1RequestTestPostRequestXWwwFormUrlencoded(
        @Field("requestFormString") requestFormString: String,
        @Field("requestFormStringNullable") requestFormStringNullable: String?,
        @Field("requestFormInt") requestFormInt: Int,
        @Field("requestFormIntNullable") requestFormIntNullable: Int?,
        @Field("requestFormDouble") requestFormDouble: Double,
        @Field("requestFormDoubleNullable") requestFormDoubleNullable: Double?,
        @Field("requestFormBoolean") requestFormBoolean: Boolean,
        @Field("requestFormBooleanNullable") requestFormBooleanNullable: Boolean?,
        @Field("requestFormStringList") requestFormStringList: List<String>,
        @Field("requestFormStringListNullable") requestFormStringListNullable: List<String>?
    ): Call<PostService1TkV1RequestTestPostRequestXWwwFormUrlencodedOutputVO?>

    data class PostService1TkV1RequestTestPostRequestXWwwFormUrlencodedOutputVO(
        @SerializedName("requestFormString")
        @Expose
        val requestFormString: String,
        @SerializedName("requestFormStringNullable")
        @Expose
        val requestFormStringNullable: String?,
        @SerializedName("requestFormInt")
        @Expose
        val requestFormInt: Int,
        @SerializedName("requestFormIntNullable")
        @Expose
        val requestFormIntNullable: Int?,
        @SerializedName("requestFormDouble")
        @Expose
        val requestFormDouble: Double,
        @SerializedName("requestFormDoubleNullable")
        @Expose
        val requestFormDoubleNullable: Double?,
        @SerializedName("requestFormBoolean")
        @Expose
        val requestFormBoolean: Boolean,
        @SerializedName("requestFormBooleanNullable")
        @Expose
        val requestFormBooleanNullable: Boolean?,
        @SerializedName("requestFormStringList")
        @Expose
        val requestFormStringList: List<String>,
        @SerializedName("requestFormStringListNullable")
        @Expose
        val requestFormStringListNullable: List<String>?
    )


    ////
    // [Post 요청(multipart/form-data) 테스트 API]
    // multipart/form-data 형태의 Request Body 를 받는 Post 메소드 요청 테스트(Multipart File List)
    // MultipartFile 파라미터가 null 이 아니라면 저장
    // (api-result-code)
    @POST("/service1/tk/v1/request-test/post-request-multipart-form-data")
    @Multipart
    fun postService1TkV1RequestTestPostRequestMultipartFormData(
        @Part requestFormString: MultipartBody.Part,
        @Part requestFormStringNullable: MultipartBody.Part?,
        @Part requestFormInt: MultipartBody.Part,
        @Part requestFormIntNullable: MultipartBody.Part?,
        @Part requestFormDouble: MultipartBody.Part,
        @Part requestFormDoubleNullable: MultipartBody.Part?,
        @Part requestFormBoolean: MultipartBody.Part,
        @Part requestFormBooleanNullable: MultipartBody.Part?,
        @Part requestFormStringList: List<MultipartBody.Part>,
        @Part requestFormStringListNullable: List<MultipartBody.Part>?,
        @Part multipartFile: MultipartBody.Part,
        @Part multipartFileNullable: MultipartBody.Part?
    ): Call<PostService1TkV1RequestTestPostRequestMultipartFormDataOutputVO?>

    data class PostService1TkV1RequestTestPostRequestMultipartFormDataOutputVO(
        @SerializedName("requestFormString")
        @Expose
        val requestFormString: String,
        @SerializedName("requestFormStringNullable")
        @Expose
        val requestFormStringNullable: String?,
        @SerializedName("requestFormInt")
        @Expose
        val requestFormInt: Int,
        @SerializedName("requestFormIntNullable")
        @Expose
        val requestFormIntNullable: Int?,
        @SerializedName("requestFormDouble")
        @Expose
        val requestFormDouble: Double,
        @SerializedName("requestFormDoubleNullable")
        @Expose
        val requestFormDoubleNullable: Double?,
        @SerializedName("requestFormBoolean")
        @Expose
        val requestFormBoolean: Boolean,
        @SerializedName("requestFormBooleanNullable")
        @Expose
        val requestFormBooleanNullable: Boolean?,
        @SerializedName("requestFormStringList")
        @Expose
        val requestFormStringList: List<String>,
        @SerializedName("requestFormStringListNullable")
        @Expose
        val requestFormStringListNullable: List<String>?
    )


    ////
    // [Post 요청(multipart/form-data list) 테스트 API]
    // multipart/form-data 형태의 Request Body 를 받는 Post 메소드 요청 테스트(Multipart File List)
    // 파일 리스트가 null 이 아니라면 저장
    // (api-result-code)
    @POST("/service1/tk/v1/request-test/post-request-multipart-form-data2")
    @Multipart
    fun postService1TkV1RequestTestPostRequestMultipartFormData2(
        @Part requestFormString: MultipartBody.Part,
        @Part requestFormStringNullable: MultipartBody.Part?,
        @Part requestFormInt: MultipartBody.Part,
        @Part requestFormIntNullable: MultipartBody.Part?,
        @Part requestFormDouble: MultipartBody.Part,
        @Part requestFormDoubleNullable: MultipartBody.Part?,
        @Part requestFormBoolean: MultipartBody.Part,
        @Part requestFormBooleanNullable: MultipartBody.Part?,
        @Part requestFormStringList: List<MultipartBody.Part>,
        @Part requestFormStringListNullable: List<MultipartBody.Part>?,
        @Part multipartFileList: List<MultipartBody.Part>,
        @Part multipartFileNullableList: List<MultipartBody.Part>?
    ): Call<PostService1TkV1RequestTestPostRequestMultipartFormData2VO?>

    data class PostService1TkV1RequestTestPostRequestMultipartFormData2VO(
        @SerializedName("requestFormString")
        @Expose
        val requestFormString: String,
        @SerializedName("requestFormStringNullable")
        @Expose
        val requestFormStringNullable: String?,
        @SerializedName("requestFormInt")
        @Expose
        val requestFormInt: Int,
        @SerializedName("requestFormIntNullable")
        @Expose
        val requestFormIntNullable: Int?,
        @SerializedName("requestFormDouble")
        @Expose
        val requestFormDouble: Double,
        @SerializedName("requestFormDoubleNullable")
        @Expose
        val requestFormDoubleNullable: Double?,
        @SerializedName("requestFormBoolean")
        @Expose
        val requestFormBoolean: Boolean,
        @SerializedName("requestFormBooleanNullable")
        @Expose
        val requestFormBooleanNullable: Boolean?,
        @SerializedName("requestFormStringList")
        @Expose
        val requestFormStringList: List<String>,
        @SerializedName("requestFormStringListNullable")
        @Expose
        val requestFormStringListNullable: List<String>?
    )


    ////
    // [Post 요청(multipart/form-data list) 테스트 API]
    // multipart/form-data 형태의 Request Body 를 받는 Post 메소드 요청 테스트(Multipart File List)
    // 파일 리스트가 null 이 아니라면 저장
    // (api-result-code)
    @POST("/service1/tk/v1/request-test/post-request-multipart-form-data-json")
    @Multipart
    fun postService1TkV1RequestTestPostRequestMultipartFormDataJson(
        @Part jsonString: MultipartBody.Part,
        @Part multipartFile: MultipartBody.Part,
        @Part multipartFileNullable: MultipartBody.Part?
    ): Call<PostService1TkV1RequestTestPostRequestMultipartFormDataJsonOutputVO?>

    data class PostService1TkV1RequestTestPostRequestMultipartFormDataJsonJsonStringVo(
        @JsonProperty("requestFormString")
        val requestFormString: String,
        @JsonProperty("requestFormStringNullable")
        val requestFormStringNullable: String?,
        @JsonProperty("requestFormInt")
        val requestFormInt: Int,
        @JsonProperty("requestFormIntNullable")
        val requestFormIntNullable: Int?,
        @JsonProperty("requestFormDouble")
        val requestFormDouble: Double,
        @JsonProperty("requestFormDoubleNullable")
        val requestFormDoubleNullable: Double?,
        @JsonProperty("requestFormBoolean")
        val requestFormBoolean: Boolean,
        @JsonProperty("requestFormBooleanNullable")
        val requestFormBooleanNullable: Boolean?,
        @JsonProperty("requestFormStringList")
        val requestFormStringList: List<String>,
        @JsonProperty("requestFormStringListNullable")
        val requestFormStringListNullable: List<String>?
    )

    data class PostService1TkV1RequestTestPostRequestMultipartFormDataJsonOutputVO(
        @SerializedName("requestFormString")
        @Expose
        val requestFormString: String,
        @SerializedName("requestFormStringNullable")
        @Expose
        val requestFormStringNullable: String?,
        @SerializedName("requestFormInt")
        @Expose
        val requestFormInt: Int,
        @SerializedName("requestFormIntNullable")
        @Expose
        val requestFormIntNullable: Int?,
        @SerializedName("requestFormDouble")
        @Expose
        val requestFormDouble: Double,
        @SerializedName("requestFormDoubleNullable")
        @Expose
        val requestFormDoubleNullable: Double?,
        @SerializedName("requestFormBoolean")
        @Expose
        val requestFormBoolean: Boolean,
        @SerializedName("requestFormBooleanNullable")
        @Expose
        val requestFormBooleanNullable: Boolean?,
        @SerializedName("requestFormStringList")
        @Expose
        val requestFormStringList: List<String>,
        @SerializedName("requestFormStringListNullable")
        @Expose
        val requestFormStringListNullable: List<String>?
    )


    ////
    // [인위적 에러 발생 테스트 API]
    // 요청 받으면 인위적인 서버 에러를 발생시킵니다.(Http Response Status 500)
    // (api-result-code)
    @POST("service1/tk/v1/request-test/generate-error")
    fun postService1TkV1RequestTestGenerateError(): Call<Unit?>


    ////
    // [결과 코드 발생 테스트 API]
    // Response Header 에 api-result-code 를 반환하는 테스트 API
    //(api-result-code)
    // 1 : errorType 을 A 로 보냈습니다.
    // 2 : errorType 을 B 로 보냈습니다.
    // 3 : errorType 을 C 로 보냈습니다.
    @POST("/service1/tk/v1/request-test/api-result-code-test")
    fun postService1TkV1RequestTestApiResultCodeTest(
        @Query("errorType") errorType: PostService1TkV1RequestTestApiResultCodeTestErrorTypeEnum
    ): Call<Unit?>

    enum class PostService1TkV1RequestTestApiResultCodeTestErrorTypeEnum {
        A,
        B,
        C
    }


    ////
    // [인위적 타임아웃 에러 발생 테스트]
    // 타임아웃 에러를 발생시키기 위해 임의로 응답 시간을 지연시킵니다.
    // (api-result-code)
    @POST("/service1/tk/v1/request-test/time-delay-test")
    fun postService1TkV1RequestTestGenerateTimeOutError(
        @Query("delayTimeSec") delayTimeSec: Long
    ): Call<Unit?>


    ////
    // [text/string 반환 샘플]
    // text/string 형식의 Response Body 를 반환합니다.
    // (api-result-code)
    @GET("/service1/tk/v1/request-test/return-text-string")
    @Headers("Content-Type: text/string")
    fun getService1TkV1RequestTestReturnTextString(): Call<String>


    ////
    // [text/html 반환 샘플]
    // text/html 형식의 Response Body 를 반환합니다.
    // (api-result-code)
    @GET("/service1/tk/v1/request-test/return-text-html")
    @Headers("Content-Type: text/html")
    fun getService1TkV1RequestTestReturnTextHtml(): Call<String>


    ////
    // [비동기 처리 결과 반환 샘플]
    // API 호출시 함수 내에서 별도 스레드로 작업을 수행하고,
    // 비동기 작업 완료 후 그 처리 결과가 반환됨
    // (api-result-code)
    @GET("/service1/tk/v1/request-test/async-result")
    fun getService1TkV1RequestTestAsyncResult(): Call<GetService1TkV1RequestTestAsyncResultOutputVO>

    data class GetService1TkV1RequestTestAsyncResultOutputVO(
        @SerializedName("resultMessage")
        @Expose
        val resultMessage: String
    )
}
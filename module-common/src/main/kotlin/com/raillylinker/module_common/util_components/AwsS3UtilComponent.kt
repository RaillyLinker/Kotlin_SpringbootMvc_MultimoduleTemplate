package com.raillylinker.module_common.util_components

import org.springframework.web.multipart.MultipartFile
import java.io.File

// [AWS S3 유틸]
interface AwsS3UtilComponent {
    // (S3 로 업로드하는 함수)
    fun upload(
        // S3 에 저장할 파일 객체
        multipartFile: MultipartFile,
        // S3 에 저장할 때 사용할 파일명 (ex : "test.png")
        fileSaveName: String,
        // 버킷 위치
        // S3 안에 가장 외곽 버킷은 직접 만들어 보안 설정 등을 거쳐야 합니다.
        // test 라는 버킷을 만들었고, 그 안에 my 디렉토리, 그 안에 bucket 디렉토리 안에 fileSaveName 이름의 파일을 저장하려면,
        // test/my/bucket 이라고 입력하면, 결과적으로 S3 -> test/my/bucket/test.png 라는 경로로 파일이 저장됩니다.
        bucketName: String
    ): String

    // (S3 로 업로드하는 함수)
    fun upload(
        // S3 에 저장할 파일 객체
        file: File,
        // S3 에 저장할 때 사용할 파일명 (ex : "test.png")
        fileSaveName: String,
        // 버킷 위치 (ex : test/my/bucket)
        bucketName: String
    ): String

    // (S3 에 저장된 파일을 삭제하는 함수)
    fun delete(
        // 버킷 위치 (ex : test/my/bucket)
        bucketName: String,
        // 삭제할 파일명 (ex : "test.png")
        fileName: String
    )

    // (S3 에 저장된 텍스트(Html 포함) 파일의 내용을 읽어오는 함수)
    fun getTextFileString(
        // 버킷 위치 (ex : test/my/bucket)
        bucketName: String,
        // 읽을 파일명 (ex : "test.png")
        fileName: String
    ): String

    // (다운로드 주소로부터 파일을 다운로드하여 S3에 업로드하고 로컬 파일을 삭제하는 함수)
    fun downloadFileAndUpload(
        fileUrl: String,
        fileSaveName: String,
        bucketName: String
    ): String
}
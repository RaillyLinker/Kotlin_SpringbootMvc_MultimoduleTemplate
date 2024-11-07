package com.raillylinker.module_common.util_components

// [HTML String 을 기반으로 PDF 파일을 생성하는 유틸]
// https://flyingsaucerproject.github.io/flyingsaucer/r8/guide/users-guide-R8.html
// PDF 로 변환할 HTML 작성시 XHTML 1.0(strict), CSS 2.1 (@page 의 size 는 가능) 를 엄격히 지켜야 합니다.
interface PdfGenerator {
    // (HTML String 을 PDF 로 변환)
    fun createPdfByteArrayFromHtmlString(
        htmlString: String, // PDF 로 변환할 HTML String (ex : <!DOCTYPE html> <html> ....)
        // 폰트 파일 맵 (키 : html 의 @font-face src 에 입력한 파일명, 값 : html 의 @font-face src 에 url('주소') 이런 형식으로 치환될 값)
        /*
             map value ex : {"NanumGothicFile.ttf" : "http://127.0.0.1:8080/test.ttf"}
             html ex :
             @font-face {
                 font-family: NanumGothic;
                 src: "NanumGothicFile.ttf";
                 -fs-pdf-font-embed: embed;
                 -fs-pdf-font-encoding: Identity-H;
             }
         */
        resourceFontFileNameMap: HashMap<String, String>,
        // 이미지 파일 맵 (키 : html 의 img src 에 입력한 파일명, 값 : 로컬에 저장된 이미지 파일 full 경로)
        /*
             map value ex : {"html_to_pdf_sample.jpg" : "C:\Dev\test.jpg"}
             html ex :
             <img src="html_to_pdf_sample.jpg" />
         */
        savedImgFilePathMap: HashMap<String, String>
    ): ByteArray
}
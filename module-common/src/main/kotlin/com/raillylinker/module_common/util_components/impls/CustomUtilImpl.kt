package com.raillylinker.module_common.util_components.impls

import com.raillylinker.module_common.util_components.CustomUtil
import org.springframework.stereotype.Component
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Path
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

// [커스텀 유틸 함수 모음]
@Component
class CustomUtilImpl : CustomUtil {
    // (디렉토리 내 파일들을 ZipOutputStream 으로 추가)
    override fun compressDirectoryToZip(directory: File, path: String, zipOutputStream: ZipOutputStream) {
        for (file in directory.listFiles() ?: emptyArray()) {
            if (file.isDirectory) {
                compressDirectoryToZip(file, "$path/${file.name}", zipOutputStream)
            } else {
                addToZip(file, "$path/${file.name}", zipOutputStream)
            }
        }
    }

    // (파일들을 ZipOutputStream 으로 추가)
    override fun addToZip(file: File, fileName: String, zipOutputStream: ZipOutputStream) {
        FileInputStream(file).use { fileInputStream ->
            val zipEntry = ZipEntry(fileName)
            zipOutputStream.putNextEntry(zipEntry)
            val buffer = ByteArray(1024)
            var length: Int
            while (fileInputStream.read(buffer).also { length = it } > 0) {
                zipOutputStream.write(buffer, 0, length)
            }
            zipOutputStream.closeEntry()
        }
    }

    // (zip 파일을 압축 풀기)
    override fun unzipFile(zipFilePath: String, destDirectory: Path) {
        FileInputStream(zipFilePath).use { fileInputStream ->
            ZipInputStream(fileInputStream).use { zipInputStream ->
                var entry: ZipEntry? = zipInputStream.nextEntry
                while (entry != null) {
                    val newFile = destDirectory.resolve(entry.name).toFile()
                    if (entry.isDirectory) {
                        newFile.mkdirs()
                    } else {
                        newFile.parentFile.mkdirs() // Ensure directory structure is created
                        FileOutputStream(newFile).use { fos ->
                            val buffer = ByteArray(1024)
                            var length: Int
                            while (zipInputStream.read(buffer).also { length = it } > 0) {
                                fos.write(buffer, 0, length)
                            }
                        }
                    }
                    zipInputStream.closeEntry()
                    entry = zipInputStream.nextEntry
                }
            }
        }
    }

    // (랜덤 영문 대소문자 + 숫자 문자열 생성)
    override fun getRandomString(length: Int): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    // (이메일 적합성 검증)
    override fun isValidEmail(email: String): Boolean {
        var err = false
        if (Pattern.compile("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$").matcher(email).matches()) {
            err = true
        }
        return err
    }

    // (ThymeLeaf 엔진으로 랜더링 한 HTML String 을 반환)
    override fun parseHtmlFileToHtmlString(justHtmlFileNameWithOutSuffix: String, variableDataMap: Map<String, Any?>): String {
        // 타임리프 resolver 설정
        val templateResolver = ClassLoaderTemplateResolver()
        templateResolver.prefix = "templates/" // static/templates 경로 아래에 있는 파일을 읽는다
        templateResolver.suffix = ".html" // .html로 끝나는 파일을 읽는다
        templateResolver.templateMode = TemplateMode.HTML // 템플릿은 html 형식

        // 스프링 template 엔진을 thymeleafResolver 를 사용하도록 설정
        val templateEngine = SpringTemplateEngine()
        templateEngine.setTemplateResolver(templateResolver)

        // 템플릿 엔진에서 사용될 변수 입력
        val context = Context()
        context.setVariables(variableDataMap)

        // 지정한 html 파일과 context 를 읽어 String 으로 반환
        return templateEngine.process(justHtmlFileNameWithOutSuffix, context)
    }

    // (byteArray 를 Hex String 으로 반환)
    override fun bytesToHex(bytes: ByteArray): String {
        val builder = StringBuilder()
        for (b in bytes) {
            builder.append(String.format("%02x", b))
        }
        return builder.toString()
    }

    // (degree 를 radian 으로)
    override fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    // (radian 을 degree 로)
    override fun rad2deg(rad: Double): Double {
        return rad * 180 / Math.PI
    }


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}
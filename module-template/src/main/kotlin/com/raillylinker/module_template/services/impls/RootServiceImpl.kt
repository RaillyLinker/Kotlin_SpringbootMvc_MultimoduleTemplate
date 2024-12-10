package com.raillylinker.module_template.services.impls

import com.raillylinker.module_template.services.RootService
import com.raillylinker.module_template.redis_map_components.redis1_main.Redis1_Map_RuntimeConfigIpList
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.servlet.ModelAndView

@Service
class RootServiceImpl(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,

    // (스웨거 문서 공개 여부 설정)
    @Value("\${springdoc.swagger-ui.enabled}") private var swaggerEnabled: Boolean,

    private val redis1RuntimeConfigIpList: Redis1_Map_RuntimeConfigIpList
) : RootService {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    override fun getRootHomePage(
        httpServletResponse: HttpServletResponse
    ): ModelAndView? {
        val mv = ModelAndView()
        mv.viewName = "home_page/home_page"

        mv.addObject(
            "viewModel",
            GetRootHomePageViewModel(
                activeProfile,
                swaggerEnabled
            )
        )

        return mv
    }

    data class GetRootHomePageViewModel(
        val env: String,
        val showApiDocumentBtn: Boolean
    )
}
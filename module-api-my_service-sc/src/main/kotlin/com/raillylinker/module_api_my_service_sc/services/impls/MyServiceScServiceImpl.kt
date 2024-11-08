package com.raillylinker.module_api_my_service_sc.services.impls

import com.raillylinker.module_api_my_service_sc.services.MyServiceScService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.servlet.ModelAndView


/*
    (세션 멤버 정보 가져오기)
    val authentication = SecurityContextHolder.getContext().authentication
    // 현 세션 멤버 이름 (비로그인 : "anonymousUser")
    val username: String = authentication.name
    // 현 세션 권한 리스트 (비로그인 : [ROLE_ANONYMOUS], 권한없음 : [])
    val roles: List<String> = authentication.authorities.map(GrantedAuthority::getAuthority)
    println("username : $username")
    println("roles : $roles")

    (세션 만료시간 설정)
    session.maxInactiveInterval = 60
    위와 같이 세션 객체에 만료시간(초) 를 설정하면 됩니다.
*/
@Service
class MyServiceScServiceImpl(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,
    // (스웨거 문서 공개 여부 설정)
    @Value("\${springdoc.swagger-ui.enabled}") private var swaggerEnabled: Boolean
) : MyServiceScService {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    override fun homePage(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        session: HttpSession
    ): ModelAndView? {
        val mv = ModelAndView()
        mv.viewName = "home_page/home_page"

        mv.addObject(
            "viewModel",
            HomePageViewModel(
                activeProfile,
                swaggerEnabled
            )
        )

        return mv
    }

    data class HomePageViewModel(
        val env: String,
        val showApiDocumentBtn: Boolean
    )
}
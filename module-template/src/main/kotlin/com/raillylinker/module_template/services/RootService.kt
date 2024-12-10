package com.raillylinker.module_template.services

import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.ModelAndView

interface RootService {
    // (루트 홈페이지 반환 함수)
    fun getRootHomePage(
        httpServletResponse: HttpServletResponse
    ): ModelAndView?
}
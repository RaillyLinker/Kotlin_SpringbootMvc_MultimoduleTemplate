package com.raillylinker.module_api_my_service_sc.services

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.springframework.web.servlet.ModelAndView

interface MyServiceScService {
    // (홈페이지 반환)
    fun homePage(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        session: HttpSession
    ): ModelAndView?
}
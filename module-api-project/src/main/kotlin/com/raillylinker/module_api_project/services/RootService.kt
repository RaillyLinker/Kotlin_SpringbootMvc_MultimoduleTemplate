package com.raillylinker.module_api_project.services

import com.raillylinker.module_api_project.controllers.RootController
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.ModelAndView

interface RootService {
    // (루트 홈페이지 반환 함수)
    fun getRootHomePage(
        httpServletResponse: HttpServletResponse
    ): ModelAndView?


    ////
    // (Project Runtime Config Redis Key-Value 모두 조회)
    fun selectAllProjectRuntimeConfigsRedisKeyValue(
        httpServletResponse: HttpServletResponse
    ): RootController.SelectAllProjectRuntimeConfigsRedisKeyValueOutputVo?


    ////
    // (Redis Project Runtime Config actuatorAllowIpList 입력)
    fun insertProjectRuntimeConfigActuatorAllowIpList(
        httpServletResponse: HttpServletResponse,
        inputVo: RootController.InsertProjectRuntimeConfigActuatorAllowIpListInputVo
    )


    ////
    // (Redis Project Runtime Config loggingDenyIpList 입력)
    fun insertProjectRuntimeConfigLoggingDenyIpList(
        httpServletResponse: HttpServletResponse,
        inputVo: RootController.InsertProjectRuntimeConfigLoggingDenyIpListInputVo
    )
}
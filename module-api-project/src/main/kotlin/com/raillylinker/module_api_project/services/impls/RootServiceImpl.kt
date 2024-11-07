package com.raillylinker.module_api_project.services.impls

import com.raillylinker.module_api_project.controllers.RootController
import com.raillylinker.module_api_project.services.RootService
import com.raillylinker.module_redis.redis_map_components.redis1_main.Redis1_Map_RuntimeConfigIpList
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
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
        mv.viewName = "root_home_page/home_page"

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


    ////
    override fun selectAllProjectRuntimeConfigsRedisKeyValue(
        httpServletResponse: HttpServletResponse
    ): RootController.SelectAllProjectRuntimeConfigsRedisKeyValueOutputVo? {
        val testEntityListVoList: MutableList<RootController.SelectAllProjectRuntimeConfigsRedisKeyValueOutputVo.KeyValueVo> =
            ArrayList()

        val actuatorIpDescVoList: MutableList<RootController.SelectAllProjectRuntimeConfigsRedisKeyValueOutputVo.KeyValueVo.IpDescVo> =
            ArrayList()

        // actuator 저장 정보 가져오기
        val keyValue =
            redis1RuntimeConfigIpList.findKeyValue(Redis1_Map_RuntimeConfigIpList.KeyEnum.ACTUATOR_ALLOW_IP_LIST.name)

        if (keyValue != null) {
            for (vl in keyValue.value.ipInfoList) {
                actuatorIpDescVoList.add(
                    RootController.SelectAllProjectRuntimeConfigsRedisKeyValueOutputVo.KeyValueVo.IpDescVo(
                        vl.ip,
                        vl.desc
                    )
                )
            }
        }

        testEntityListVoList.add(
            RootController.SelectAllProjectRuntimeConfigsRedisKeyValueOutputVo.KeyValueVo(
                Redis1_Map_RuntimeConfigIpList.KeyEnum.ACTUATOR_ALLOW_IP_LIST.name,
                actuatorIpDescVoList
            )
        )

        // 전체 조회 테스트
        val loggingDenyInfo =
            redis1RuntimeConfigIpList.findKeyValue(Redis1_Map_RuntimeConfigIpList.KeyEnum.LOGGING_DENY_IP_LIST.name)

        if (loggingDenyInfo != null) {
            val ipDescVoList: MutableList<RootController.SelectAllProjectRuntimeConfigsRedisKeyValueOutputVo.KeyValueVo.IpDescVo> =
                ArrayList()
            for (ipInfo in loggingDenyInfo.value.ipInfoList) {
                ipDescVoList.add(
                    RootController.SelectAllProjectRuntimeConfigsRedisKeyValueOutputVo.KeyValueVo.IpDescVo(
                        ipInfo.ip,
                        ipInfo.desc
                    )
                )
            }

            testEntityListVoList.add(
                RootController.SelectAllProjectRuntimeConfigsRedisKeyValueOutputVo.KeyValueVo(
                    Redis1_Map_RuntimeConfigIpList.KeyEnum.LOGGING_DENY_IP_LIST.name,
                    ipDescVoList
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return RootController.SelectAllProjectRuntimeConfigsRedisKeyValueOutputVo(testEntityListVoList)
    }


    ////
    override fun insertProjectRuntimeConfigActuatorAllowIpList(
        httpServletResponse: HttpServletResponse,
        inputVo: RootController.InsertProjectRuntimeConfigActuatorAllowIpListInputVo
    ) {
        val ipDescVoList: MutableList<Redis1_Map_RuntimeConfigIpList.ValueVo.IpDescVo> =
            java.util.ArrayList()

        for (ipDescInfo in inputVo.ipInfoList) {
            ipDescVoList.add(Redis1_Map_RuntimeConfigIpList.ValueVo.IpDescVo(ipDescInfo.ip, ipDescInfo.desc))
        }

        redis1RuntimeConfigIpList.saveKeyValue(
            Redis1_Map_RuntimeConfigIpList.KeyEnum.ACTUATOR_ALLOW_IP_LIST.name,
            Redis1_Map_RuntimeConfigIpList.ValueVo(ipDescVoList),
            null
        )
        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun insertProjectRuntimeConfigLoggingDenyIpList(
        httpServletResponse: HttpServletResponse,
        inputVo: RootController.InsertProjectRuntimeConfigLoggingDenyIpListInputVo
    ) {
        val ipDescVoList: MutableList<Redis1_Map_RuntimeConfigIpList.ValueVo.IpDescVo> =
            java.util.ArrayList()

        for (ipDescInfo in inputVo.ipInfoList) {
            ipDescVoList.add(
                Redis1_Map_RuntimeConfigIpList.ValueVo.IpDescVo(
                    ipDescInfo.ip,
                    ipDescInfo.desc
                )
            )
        }

        redis1RuntimeConfigIpList.saveKeyValue(
            Redis1_Map_RuntimeConfigIpList.KeyEnum.LOGGING_DENY_IP_LIST.name,
            Redis1_Map_RuntimeConfigIpList.ValueVo(ipDescVoList),
            null
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }
}
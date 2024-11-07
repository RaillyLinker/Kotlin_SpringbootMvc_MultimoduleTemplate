package com.raillylinker.module_app.filters

import com.raillylinker.module_redis.redis_map_components.redis1_main.Redis1_Map_RuntimeConfigIpList
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

// [민감한 정보를 지닌 actuator 접근 제한 필터]
// /actuator 로 시작되는 경로에 대한 모든 요청은,
// ApplicationRuntimeConfigs.runtimeConfigData.actuatorAllowIpList
// 위 변수에 담겨있는 IP 만을 허용하고, 나머지 접근은 404 를 반환하도록 처리하였습니다.
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class ActuatorEndpointFilter(
    // (Redis Repository)
    private val redis1RuntimeConfigIpList: Redis1_Map_RuntimeConfigIpList
) : Filter {
    override fun doFilter(
        request: ServletRequest, response: ServletResponse, chain: FilterChain
    ) {
        val httpServletRequest = (request as HttpServletRequest)
        val httpServletResponse = (response as HttpServletResponse)

        // 요청자 Ip (ex : 127.0.0.1)
        val clientAddressIp = httpServletRequest.remoteAddr

        val actuatorAllowIpInfo = try {
            redis1RuntimeConfigIpList.findKeyValue(Redis1_Map_RuntimeConfigIpList.KeyEnum.ACTUATOR_ALLOW_IP_LIST.name)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        var actuatorAllow = false
        if (actuatorAllowIpInfo != null) {
            for (actuatorAllowIp in actuatorAllowIpInfo.value.ipInfoList) {
                if (clientAddressIp == actuatorAllowIp.ip) {
                    actuatorAllow = true
                    break
                }
            }
        }

        // 리퀘스트 URI (ex : /sample/test) 가 /actuator 로 시작되는지를 확인 후 블록
        if (httpServletRequest.requestURI.startsWith("/actuator") && !actuatorAllow) {
            // status 404 반환 및 무동작
            httpServletResponse.status = HttpStatus.NOT_FOUND.value()
            return
        }

        chain.doFilter(request, response)
    }
}
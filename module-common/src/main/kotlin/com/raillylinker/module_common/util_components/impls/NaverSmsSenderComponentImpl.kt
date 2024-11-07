package com.raillylinker.module_common.util_components.impls

import com.raillylinker.module_common.util_components.NaverSmsSenderComponent
import com.raillylinker.module_retrofit2.retrofit2_classes.RepositoryNetworkRetrofit2
import com.raillylinker.module_retrofit2.retrofit2_classes.request_apis.SensApigwNtrussComRequestApi
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

// [Naver SMS 발송 유틸 객체]
@Component
class NaverSmsSenderComponentImpl(
    @Value("\${custom-config.naverSms.access-key}")
    private var accessKey: String,
    @Value("\${custom-config.naverSms.secret-key}")
    private var secretKey: String,
    @Value("\${custom-config.naverSms.service-id}")
    private var serviceId: String,
    @Value("\${custom-config.naverSms.phone-number}")
    private var phoneNumber: String,
    @Value("\${custom-config.naverSms.alim-talk-service-id}")
    private var alimTalkServiceId: String
) : NaverSmsSenderComponent {
    // <멤버 변수 공간>
    // Retrofit2 요청 객체
    private val networkRetrofit2: RepositoryNetworkRetrofit2 = RepositoryNetworkRetrofit2.getInstance()


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    override fun sendSms(inputVo: NaverSmsSenderComponent.SendSmsInputVo): Boolean {
        val time = System.currentTimeMillis()
        val responseObj = networkRetrofit2.sensApigwNtrussComRequestApi.postSmsV2ServicesNaverSmsServiceIdMessages(
            serviceId,
            time.toString(),
            accessKey,
            Base64.encodeBase64String(
                Mac.getInstance("HmacSHA256").apply {
                    this.init(SecretKeySpec(secretKey.toByteArray(charset("UTF-8")), "HmacSHA256"))
                }.doFinal(
                    StringBuilder()
                        .append("POST")
                        .append(" ")
                        .append("/sms/v2/services/$serviceId/messages")
                        .append("\n")
                        .append(time.toString())
                        .append("\n")
                        .append(accessKey)
                        .toString().toByteArray(charset("UTF-8"))
                )
            ),
            SensApigwNtrussComRequestApi.PostSmsV2ServicesNaverSmsServiceIdMessagesInputVO(
                inputVo.messageType,
                null,
                inputVo.countryCode,
                phoneNumber,
                null,
                inputVo.content,
                listOf(
                    SensApigwNtrussComRequestApi.PostSmsV2ServicesNaverSmsServiceIdMessagesInputVO.MessageVo(
                        inputVo.phoneNumber,
                        null,
                        null
                    )
                ),
                null,
                null,
                null
            )
        ).execute()

        return responseObj.code() == 202
    }


    // (알림톡 보내기)
    override fun sendAlimTalk(inputVo: NaverSmsSenderComponent.SendAlimTalkInputVo): NaverSmsSenderComponent.SendAlimTalkOutputVo? {
        if (inputVo.messages.size > 100) {
            return null
        }

        val time = System.currentTimeMillis()
        val messageVoList: ArrayList<SensApigwNtrussComRequestApi.PostAlimtalkV2ServicesNaverSmsServiceIdMessagesInputVO.MessageVo> =
            arrayListOf()
        for (message in inputVo.messages) {
            messageVoList.add(
                SensApigwNtrussComRequestApi.PostAlimtalkV2ServicesNaverSmsServiceIdMessagesInputVO.MessageVo(
                    message.countryCode,
                    message.phoneNumber,
                    message.title,
                    message.content,
                    message.headerContent,
                    if (message.itemHighlight == null) {
                        null
                    } else {
                        SensApigwNtrussComRequestApi.PostAlimtalkV2ServicesNaverSmsServiceIdMessagesInputVO.MessageVo.ItemHighlightVo(
                            message.itemHighlight.title,
                            message.itemHighlight.description
                        )
                    },
                    if (message.item == null) {
                        null
                    } else {
                        val voList: ArrayList<SensApigwNtrussComRequestApi.PostAlimtalkV2ServicesNaverSmsServiceIdMessagesInputVO.MessageVo.ItemVo.ListItemVo> =
                            arrayListOf()
                        for (item in message.item.list) {
                            voList.add(
                                SensApigwNtrussComRequestApi.PostAlimtalkV2ServicesNaverSmsServiceIdMessagesInputVO.MessageVo.ItemVo.ListItemVo(
                                    item.title,
                                    item.description
                                )
                            )
                        }
                        SensApigwNtrussComRequestApi.PostAlimtalkV2ServicesNaverSmsServiceIdMessagesInputVO.MessageVo.ItemVo(
                            voList,
                            if (message.item.summary == null) {
                                null
                            } else {
                                SensApigwNtrussComRequestApi.PostAlimtalkV2ServicesNaverSmsServiceIdMessagesInputVO.MessageVo.ItemVo.SummaryVo(
                                    message.item.summary.title,
                                    message.item.summary.description
                                )
                            }
                        )
                    },
                    if (message.buttons == null) {
                        null
                    } else {
                        val buttonList: ArrayList<SensApigwNtrussComRequestApi.PostAlimtalkV2ServicesNaverSmsServiceIdMessagesInputVO.MessageVo.ButtonVo> =
                            arrayListOf()
                        for (button in message.buttons) {
                            buttonList.add(
                                SensApigwNtrussComRequestApi.PostAlimtalkV2ServicesNaverSmsServiceIdMessagesInputVO.MessageVo.ButtonVo(
                                    button.type,
                                    button.name,
                                    button.linkMobile,
                                    button.linkPc,
                                    button.schemeIos,
                                    button.schemeAndroid
                                )
                            )
                        }
                        buttonList
                    },
                    message.useSmsFailover,
                    if (message.failoverConfig == null) {
                        null
                    } else {
                        SensApigwNtrussComRequestApi.PostAlimtalkV2ServicesNaverSmsServiceIdMessagesInputVO.MessageVo.FailOverConfigVo(
                            message.failoverConfig!!.type,
                            message.failoverConfig!!.from,
                            message.failoverConfig!!.subject,
                            message.failoverConfig!!.content
                        )
                    }
                )
            )
        }
        val responseObj = networkRetrofit2.sensApigwNtrussComRequestApi.postAlimtalkV2ServicesNaverSmsServiceIdMessages(
            alimTalkServiceId,
            time.toString(),
            accessKey,
            Base64.encodeBase64String(
                Mac.getInstance("HmacSHA256").apply {
                    this.init(SecretKeySpec(secretKey.toByteArray(charset("UTF-8")), "HmacSHA256"))
                }.doFinal(
                    StringBuilder()
                        .append("POST")
                        .append(" ")
                        .append("/alimtalk/v2/services/$alimTalkServiceId/messages")
                        .append("\n")
                        .append(time.toString())
                        .append("\n")
                        .append(accessKey)
                        .toString().toByteArray(charset("UTF-8"))
                )
            ),
            SensApigwNtrussComRequestApi.PostAlimtalkV2ServicesNaverSmsServiceIdMessagesInputVO(
                inputVo.plusFriendId,
                inputVo.templateCode,
                messageVoList,
                null,
                null
            )
        ).execute()

        return if (responseObj.code() == 202) {
            val responseBody = responseObj.body()!!
            val messageResults: ArrayList<NaverSmsSenderComponent.SendAlimTalkOutputVo.MessageResultVo> = arrayListOf()

            for (message in responseBody.messages) {
                messageResults.add(
                    NaverSmsSenderComponent.SendAlimTalkOutputVo.MessageResultVo(
                        message.countryCode,
                        message.to,
                        message.requestStatusCode,
                        message.requestStatusDesc
                    )
                )
            }
            NaverSmsSenderComponent.SendAlimTalkOutputVo(
                messageResults
            )
        } else {
            null
        }
    }


    // ---------------------------------------------------------------------------------------------
    // <비공개 메소드 공간>


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
}
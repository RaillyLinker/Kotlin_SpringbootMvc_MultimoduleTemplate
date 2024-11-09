package com.raillylinker.module_api_my_service_tk_sample.services

import com.raillylinker.module_api_my_service_tk_sample.controllers.MyServiceTkSampleMapCoordinateCalculationController
import jakarta.servlet.http.HttpServletResponse

interface MyServiceTkSampleMapCoordinateCalculationService {
    // (테스트용 기본 좌표 리스트를 DB에 저장)
    fun insertDefaultCoordinateDataToDatabase(httpServletResponse: HttpServletResponse)


    ////
    // (두 좌표 사이의 거리를 반환(하버사인 공식))
    fun getDistanceMeterBetweenTwoCoordinate(
        httpServletResponse: HttpServletResponse,
        latitude1: Double,
        longitude1: Double,
        latitude2: Double,
        longitude2: Double
    ): MyServiceTkSampleMapCoordinateCalculationController.GetDistanceMeterBetweenTwoCoordinateOutputVo?


    ////
    // (두 좌표 사이의 거리를 반환(Vincenty 공식))
    fun getDistanceMeterBetweenTwoCoordinateVincenty(
        httpServletResponse: HttpServletResponse,
        latitude1: Double,
        longitude1: Double,
        latitude2: Double,
        longitude2: Double
    ): MyServiceTkSampleMapCoordinateCalculationController.GetDistanceMeterBetweenTwoCoordinateVincentyOutputVo?


    ////
    // (좌표들 사이의 중심 좌표를 반환)
    fun returnCenterCoordinate(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleMapCoordinateCalculationController.ReturnCenterCoordinateInputVo
    ): MyServiceTkSampleMapCoordinateCalculationController.ReturnCenterCoordinateOutputVo?


    ////
    // (DB 의 좌표 테이블에 좌표 정보를 저장)
    fun insertCoordinateDataToDatabase(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleMapCoordinateCalculationController.InsertCoordinateDataToDatabaseInputVo
    ): MyServiceTkSampleMapCoordinateCalculationController.InsertCoordinateDataToDatabaseOutputVo?


    ////
    // (DB 의 좌표 테이블의 모든 데이터 삭제)
    fun deleteAllCoordinateDataFromDatabase(httpServletResponse: HttpServletResponse)


    ////
    // (DB 에 저장된 좌표들을 SQL 을 사용하여, 기준 좌표의 N Km 내의 결과만 필터)
    fun selectCoordinateDataRowsInRadiusKiloMeterSample(
        httpServletResponse: HttpServletResponse,
        anchorLatitude: Double,
        anchorLongitude: Double,
        radiusKiloMeter: Double
    ): MyServiceTkSampleMapCoordinateCalculationController.SelectCoordinateDataRowsInRadiusKiloMeterSampleOutputVo?


    ////
    // (DB 에 저장된 좌표들을 SQL 을 사용하여, 북서 좌표에서 남동 좌표까지의 사각 영역 안에 들어오는 좌표들만 필터링하여 반환)
    fun selectCoordinateDataRowsInCoordinateBoxSample(
        httpServletResponse: HttpServletResponse,
        northLatitude: Double, // 북위도 (ex : 37.771848)
        eastLongitude: Double, // 동경도 (ex : 127.433549)
        southLatitude: Double, // 남위도 (ex : 37.245683)
        westLongitude: Double // 남경도 (ex : 126.587602)
    ): MyServiceTkSampleMapCoordinateCalculationController.SelectCoordinateDataRowsInCoordinateBoxSampleOutputVo?
}
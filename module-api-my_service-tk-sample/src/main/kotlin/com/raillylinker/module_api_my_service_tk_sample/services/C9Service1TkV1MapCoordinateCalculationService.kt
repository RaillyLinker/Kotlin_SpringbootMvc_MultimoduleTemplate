package com.raillylinker.module_api_my_service_tk_sample.services

import com.raillylinker.module_api_my_service_tk_sample.controllers.C9Service1TkV1MapCoordinateCalculationController
import jakarta.servlet.http.HttpServletResponse

interface C9Service1TkV1MapCoordinateCalculationService {
    fun api0InsertDefaultCoordinateDataToDatabase(httpServletResponse: HttpServletResponse)


    ////
    fun api1GetDistanceMeterBetweenTwoCoordinate(
        httpServletResponse: HttpServletResponse,
        latitude1: Double,
        longitude1: Double,
        latitude2: Double,
        longitude2: Double
    ): C9Service1TkV1MapCoordinateCalculationController.Api1GetDistanceMeterBetweenTwoCoordinateOutputVo?


    ////
    fun api1Dot1GetDistanceMeterBetweenTwoCoordinateVincenty(
        httpServletResponse: HttpServletResponse,
        latitude1: Double,
        longitude1: Double,
        latitude2: Double,
        longitude2: Double
    ): C9Service1TkV1MapCoordinateCalculationController.Api1Dot1GetDistanceMeterBetweenTwoCoordinateVincentyOutputVo?


    ////
    fun api2ReturnCenterCoordinate(
        httpServletResponse: HttpServletResponse,
        inputVo: C9Service1TkV1MapCoordinateCalculationController.Api2ReturnCenterCoordinateInputVo
    ): C9Service1TkV1MapCoordinateCalculationController.Api2ReturnCenterCoordinateOutputVo?


    ////
    fun api3InsertCoordinateDataToDatabase(
        httpServletResponse: HttpServletResponse,
        inputVo: C9Service1TkV1MapCoordinateCalculationController.Api3InsertCoordinateDataToDatabaseInputVo
    ): C9Service1TkV1MapCoordinateCalculationController.Api3InsertCoordinateDataToDatabaseOutputVo?


    ////
    fun api4DeleteAllCoordinateDataFromDatabase(httpServletResponse: HttpServletResponse)


    ////
    fun api5SelectCoordinateDataRowsInRadiusKiloMeterSample(
        httpServletResponse: HttpServletResponse,
        anchorLatitude: Double,
        anchorLongitude: Double,
        radiusKiloMeter: Double
    ): C9Service1TkV1MapCoordinateCalculationController.Api5SelectCoordinateDataRowsInRadiusKiloMeterSampleOutputVo?


    ////
    fun api6SelectCoordinateDataRowsInCoordinateBoxSample(
        httpServletResponse: HttpServletResponse,
        northLatitude: Double, // 북위도 (ex : 37.771848)
        eastLongitude: Double, // 동경도 (ex : 127.433549)
        southLatitude: Double, // 남위도 (ex : 37.245683)
        westLongitude: Double // 남경도 (ex : 126.587602)
    ): C9Service1TkV1MapCoordinateCalculationController.Api6SelectCoordinateDataRowsInCoordinateBoxSampleOutputVo?
}
package com.raillylinker.module_api_my_service_tk_sample.services.impls

import com.raillylinker.module_api_my_service_tk_sample.controllers.C9Service1TkV1MapCoordinateCalculationController
import com.raillylinker.module_api_my_service_tk_sample.services.C9Service1TkV1MapCoordinateCalculationService
import com.raillylinker.module_common.util_components.MapCoordinateUtil
import com.raillylinker.module_jpa.annotations.CustomTransactional
import com.raillylinker.module_jpa.configurations.jpa_configs.Db1MainConfig
import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.Db1_Template_TestMap
import com.raillylinker.module_jpa.jpa_beans.db1_main.repositories.Db1_Native_Repository
import com.raillylinker.module_jpa.jpa_beans.db1_main.repositories.Db1_Template_TestMap_Repository
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class C9Service1TkV1MapCoordinateCalculationServiceImpl(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,

    private val mapCoordinateUtil: MapCoordinateUtil,

    // (Database Repository)
    private val db1TemplateTestMapRepository: Db1_Template_TestMap_Repository,
    private val db1NativeRepository: Db1_Native_Repository
) : C9Service1TkV1MapCoordinateCalculationService {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api0InsertDefaultCoordinateDataToDatabase(httpServletResponse: HttpServletResponse) {
        db1TemplateTestMapRepository.deleteAll()

        val latLngList: List<Pair<Double, Double>> = listOf(
            Pair(37.5845885, 127.0001891),
            Pair(37.6060504, 126.9607987),
            Pair(37.5844214, 126.9699813),
            Pair(37.5757558, 126.9710255),
            Pair(37.5764907, 126.968655),
            Pair(37.5786667, 127.0156223),
            Pair(37.561697, 126.9968491),
            Pair(37.5880051, 127.0181872),
            Pair(37.5713246, 126.9635654),
            Pair(37.5922066, 127.0135319),
            Pair(37.5690038, 126.9632755),
            Pair(37.584865, 126.948639),
            Pair(37.5690454, 127.0232121),
            Pair(37.5634635, 127.015948),
            Pair(37.5748642, 127.0155003),
            Pair(37.5708604, 126.9612919),
            Pair(37.5570078, 126.9533333),
            Pair(37.5726188, 127.0576283),
            Pair(37.5914225, 127.0129648),
            Pair(37.5659102, 127.0217363)
        )

        for (latLng in latLngList) {
            db1TemplateTestMapRepository.save(
                Db1_Template_TestMap(
                    latLng.first,
                    latLng.second
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun api1GetDistanceMeterBetweenTwoCoordinate(
        httpServletResponse: HttpServletResponse,
        latitude1: Double,
        longitude1: Double,
        latitude2: Double,
        longitude2: Double
    ): C9Service1TkV1MapCoordinateCalculationController.Api1GetDistanceMeterBetweenTwoCoordinateOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return C9Service1TkV1MapCoordinateCalculationController.Api1GetDistanceMeterBetweenTwoCoordinateOutputVo(
            mapCoordinateUtil.getDistanceMeterBetweenTwoLatLngCoordinateHarversine(
                Pair(latitude1, longitude1),
                Pair(latitude2, longitude2)
            )
        )
    }


    ////
    override fun api1Dot1GetDistanceMeterBetweenTwoCoordinateVincenty(
        httpServletResponse: HttpServletResponse,
        latitude1: Double,
        longitude1: Double,
        latitude2: Double,
        longitude2: Double
    ): C9Service1TkV1MapCoordinateCalculationController.Api1Dot1GetDistanceMeterBetweenTwoCoordinateVincentyOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return C9Service1TkV1MapCoordinateCalculationController.Api1Dot1GetDistanceMeterBetweenTwoCoordinateVincentyOutputVo(
            mapCoordinateUtil.getDistanceMeterBetweenTwoLatLngCoordinateVincenty(
                Pair(latitude1, longitude1),
                Pair(latitude2, longitude2)
            )
        )
    }


    ////
    override fun api2ReturnCenterCoordinate(
        httpServletResponse: HttpServletResponse,
        inputVo: C9Service1TkV1MapCoordinateCalculationController.Api2ReturnCenterCoordinateInputVo
    ): C9Service1TkV1MapCoordinateCalculationController.Api2ReturnCenterCoordinateOutputVo? {
        val latLngCoordinate = ArrayList<Pair<Double, Double>>()

        for (coordinate in inputVo.coordinateList) {
            latLngCoordinate.add(
                Pair(coordinate.centerLatitude, coordinate.centerLongitude)
            )
        }

        val centerCoordinate = mapCoordinateUtil.getCenterLatLngCoordinate(
            latLngCoordinate
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return C9Service1TkV1MapCoordinateCalculationController.Api2ReturnCenterCoordinateOutputVo(
            centerCoordinate.first,
            centerCoordinate.second
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api3InsertCoordinateDataToDatabase(
        httpServletResponse: HttpServletResponse,
        inputVo: C9Service1TkV1MapCoordinateCalculationController.Api3InsertCoordinateDataToDatabaseInputVo
    ): C9Service1TkV1MapCoordinateCalculationController.Api3InsertCoordinateDataToDatabaseOutputVo? {
        db1TemplateTestMapRepository.save(
            Db1_Template_TestMap(
                inputVo.latitude,
                inputVo.longitude
            )
        )

        val coordinateList =
            ArrayList<C9Service1TkV1MapCoordinateCalculationController.Api3InsertCoordinateDataToDatabaseOutputVo.Coordinate>()
        val latLngCoordinate = ArrayList<Pair<Double, Double>>()

        for (testMap in db1TemplateTestMapRepository.findAll()) {
            coordinateList.add(
                C9Service1TkV1MapCoordinateCalculationController.Api3InsertCoordinateDataToDatabaseOutputVo.Coordinate(
                    testMap.latitude,
                    testMap.longitude
                )
            )

            latLngCoordinate.add(
                Pair(testMap.latitude, testMap.longitude)
            )
        }

        val centerCoordinate = mapCoordinateUtil.getCenterLatLngCoordinate(
            latLngCoordinate
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return C9Service1TkV1MapCoordinateCalculationController.Api3InsertCoordinateDataToDatabaseOutputVo(
            coordinateList,
            C9Service1TkV1MapCoordinateCalculationController.Api3InsertCoordinateDataToDatabaseOutputVo.Coordinate(
                centerCoordinate.first,
                centerCoordinate.second
            )
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun api4DeleteAllCoordinateDataFromDatabase(httpServletResponse: HttpServletResponse) {
        db1TemplateTestMapRepository.deleteAll()

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun api5SelectCoordinateDataRowsInRadiusKiloMeterSample(
        httpServletResponse: HttpServletResponse,
        anchorLatitude: Double,
        anchorLongitude: Double,
        radiusKiloMeter: Double
    ): C9Service1TkV1MapCoordinateCalculationController.Api5SelectCoordinateDataRowsInRadiusKiloMeterSampleOutputVo? {
        val entityList =
            db1NativeRepository.forC9N5(
                anchorLatitude,
                anchorLongitude,
                radiusKiloMeter
            )

        val coordinateCalcResultList =
            ArrayList<C9Service1TkV1MapCoordinateCalculationController.Api5SelectCoordinateDataRowsInRadiusKiloMeterSampleOutputVo.CoordinateCalcResult>()
        for (entity in entityList) {
            coordinateCalcResultList.add(
                C9Service1TkV1MapCoordinateCalculationController.Api5SelectCoordinateDataRowsInRadiusKiloMeterSampleOutputVo.CoordinateCalcResult(
                    entity.uid,
                    entity.latitude,
                    entity.longitude,
                    entity.distanceKiloMeter
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return C9Service1TkV1MapCoordinateCalculationController.Api5SelectCoordinateDataRowsInRadiusKiloMeterSampleOutputVo(
            coordinateCalcResultList
        )
    }


    ////
    override fun api6SelectCoordinateDataRowsInCoordinateBoxSample(
        httpServletResponse: HttpServletResponse,
        northLatitude: Double, // 북위도 (ex : 37.771848)
        eastLongitude: Double, // 동경도 (ex : 127.433549)
        southLatitude: Double, // 남위도 (ex : 37.245683)
        westLongitude: Double // 남경도 (ex : 126.587602)
    ): C9Service1TkV1MapCoordinateCalculationController.Api6SelectCoordinateDataRowsInCoordinateBoxSampleOutputVo? {
        val entityList =
            db1NativeRepository.forC9N6(
                northLatitude,
                eastLongitude,
                southLatitude,
                westLongitude
            )

        val coordinateCalcResultList =
            ArrayList<C9Service1TkV1MapCoordinateCalculationController.Api6SelectCoordinateDataRowsInCoordinateBoxSampleOutputVo.CoordinateCalcResult>()
        for (entity in entityList) {
            coordinateCalcResultList.add(
                C9Service1TkV1MapCoordinateCalculationController.Api6SelectCoordinateDataRowsInCoordinateBoxSampleOutputVo.CoordinateCalcResult(
                    entity.uid,
                    entity.latitude,
                    entity.longitude
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return C9Service1TkV1MapCoordinateCalculationController.Api6SelectCoordinateDataRowsInCoordinateBoxSampleOutputVo(
            coordinateCalcResultList
        )
    }
}
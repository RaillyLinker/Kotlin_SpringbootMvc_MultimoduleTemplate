package com.raillylinker.module_common.util_components.impls

import com.raillylinker.module_common.util_components.MapCoordinateUtil
import org.springframework.stereotype.Component
import kotlin.math.*

// [지도 좌표계 관련 유틸]
@Component
class MapCoordinateUtilImpl : MapCoordinateUtil {
    // (지도 좌표 1 에서 지도 좌표 2 까지의 거리 (미터) 반환, 하버사인 공식)
    override fun getDistanceMeterBetweenTwoLatLngCoordinateHarversine(
        latlng1: Pair<Double, Double>,
        latlng2: Pair<Double, Double>
    ): Double {
        val r = 6371e3  // 지구 반지름 (미터 단위)
        val lat1Rad = latlng1.first * PI / 180 // deg to rad
        val lat2Rad = latlng2.first * PI / 180 // deg to rad
        val deltaLat = (latlng2.first - latlng1.first) * PI / 180 // deg to rad
        val deltaLon = (latlng2.second - latlng1.second) * PI / 180 // deg to rad

        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(deltaLon / 2) * sin(deltaLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return r * c
    }

    // (지도 좌표 1 에서 지도 좌표 2 까지의 거리 (미터) 반환, Vincenty 공식)
    // Vincenty 공식은 타원체 위에서 두 좌표 사이의 거리를 계산하는 방법으로, 지구를 완전한 구가 아닌 타원체로 간주하기 때문에 더 정확한 결과를 제공합니다.
    // 좌표계는 WGS84 를 사용합니다.
    override fun getDistanceMeterBetweenTwoLatLngCoordinateVincenty(
        latlng1: Pair<Double, Double>,
        latlng2: Pair<Double, Double>
    ): Double {
        val a = 6378137.0 // WGS-84 기준 타원체의 장반경 (미터)
        val b = 6356752.314245 // 단반경
        val f = 0.0033528106647756 // WGS-84 타원체의 편평률

        val lat1 = latlng1.first * PI / 180
        val lat2 = latlng2.first * PI / 180
        val lon1 = latlng1.second * PI / 180
        val lon2 = latlng2.second * PI / 180

        val U1 = atan((1 - f) * tan(lat1))
        val U2 = atan((1 - f) * tan(lat2))
        val L = lon2 - lon1
        var lambda = L

        val maxIterations = 200
        val tolerance = 1e-12
        var cosSqAlpha: Double
        var sinSigma: Double
        var cos2SigmaM: Double
        var cosSigma: Double
        var sigma: Double
        var lambdaP: Double
        var iter = 0

        do {
            val sinLambda = sin(lambda)
            val cosLambda = cos(lambda)
            sinSigma = sqrt(
                (cos(U2) * sinLambda) * (cos(U2) * sinLambda) +
                        (cos(U1) * sin(U2) - sin(U1) * cos(U2) * cosLambda) *
                        (cos(U1) * sin(U2) - sin(U1) * cos(U2) * cosLambda)
            )

            if (sinSigma == 0.0) return 0.0 // 두 좌표가 동일함

            cosSigma = sin(U1) * sin(U2) + cos(U1) * cos(U2) * cosLambda
            sigma = atan2(sinSigma, cosSigma)
            val sinAlpha = cos(U1) * cos(U2) * sinLambda / sinSigma
            cosSqAlpha = 1 - sinAlpha * sinAlpha
            cos2SigmaM = if (cosSqAlpha != 0.0) cosSigma - 2 * sin(U1) * sin(U2) / cosSqAlpha else 0.0
            val C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha))
            lambdaP = lambda
            lambda = L + (1 - C) * f * sinAlpha * (sigma + C * sinSigma * (
                    cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)))
        } while (abs(lambda - lambdaP) > tolerance && ++iter < maxIterations)

        if (iter >= maxIterations) return Double.NaN // 수렴 실패

        val uSq = cosSqAlpha * (a * a - b * b) / (b * b)
        val A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)))
        val B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)))
        val deltaSigma = B * sinSigma * (
                cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) -
                        B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) *
                        (-3 + 4 * cos2SigmaM * cos2SigmaM)))

        val s = b * A * (sigma - deltaSigma)

        return s // 거리 (미터)
    }

    // (여러 지도 좌표들의 중심 좌표(Latitude, Longitude) 반환)
    override fun getCenterLatLngCoordinate(latLngList: List<Pair<Double, Double>>): Pair<Double, Double> {
        if (latLngList.isEmpty()) {
            throw IllegalArgumentException("The list must not be empty")
        }

        var xSum = 0.0
        var ySum = 0.0
        var zSum = 0.0

        for (latLng in latLngList) {
            val latRad = latLng.first * PI / 180
            val lonRad = latLng.second * PI / 180

            xSum += cos(latRad) * cos(lonRad)
            ySum += cos(latRad) * sin(lonRad)
            zSum += sin(latRad)
        }

        val total = latLngList.size

        val avgX = xSum / total
        val avgY = ySum / total
        val avgZ = zSum / total

        val centralLon = atan2(avgY, avgX)
        val hypotenuse = sqrt(avgX * avgX + avgY * avgY)
        val centralLat = atan2(avgZ, hypotenuse)

        return Pair(centralLat * 180 / PI, centralLon * 180 / PI)
    }
}
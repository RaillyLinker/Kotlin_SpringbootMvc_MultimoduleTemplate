package com.raillylinker.module_common.util_components

// [지도 좌표계 관련 유틸]
interface MapCoordinateUtil {
    // (지도 좌표 1 에서 지도 좌표 2 까지의 거리 (미터) 반환, 하버사인 공식)
    fun getDistanceMeterBetweenTwoLatLngCoordinateHarversine(
        latlng1: Pair<Double, Double>,
        latlng2: Pair<Double, Double>
    ): Double

    // (지도 좌표 1 에서 지도 좌표 2 까지의 거리 (미터) 반환, Vincenty 공식)
    // Vincenty 공식은 타원체 위에서 두 좌표 사이의 거리를 계산하는 방법으로, 지구를 완전한 구가 아닌 타원체로 간주하기 때문에 더 정확한 결과를 제공합니다.
    // 좌표계는 WGS84 를 사용합니다.
    fun getDistanceMeterBetweenTwoLatLngCoordinateVincenty(
        latlng1: Pair<Double, Double>,
        latlng2: Pair<Double, Double>
    ): Double

    // (여러 지도 좌표들의 중심 좌표(Latitude, Longitude) 반환)
    fun getCenterLatLngCoordinate(latLngList: List<Pair<Double, Double>>): Pair<Double, Double>
}
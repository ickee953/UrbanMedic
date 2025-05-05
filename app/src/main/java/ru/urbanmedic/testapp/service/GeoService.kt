/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 5 May 2025
 */

package ru.urbanmedic.testapp.service

import ru.urbanmedic.testapp.data.api.GeoHelper
import ru.urbanmedic.testapp.data.api.RetrofitBuilder
import ru.urbanmedic.testapp.repository.GeoRepository
import ru.urbanmedic.testapp.vo.GeoVO

class GeoService(
    private val geoRepository: GeoRepository = GeoRepository(GeoHelper(RetrofitBuilder.geoService))
) {

    suspend fun loadCityByGeo(lat: Double, lon: Double): String? {
        try {
            val response = geoRepository.getCity(GeoVO(lat, lon))

            if( response.code() == 200 ){
                val suggestionsList = response.body()!!.suggestionsVO
                if(suggestionsList.isNotEmpty()) {
                    return suggestionsList[0].suggestionDataVO.city
                }
            } else {
                return ""
            }
        } catch (exception : Exception){
            exception.printStackTrace()
        }

        return null
    }

}
package ru.urbanmedic.testapp.repository

import ru.urbanmedic.testapp.data.api.GeoHelper
import ru.urbanmedic.testapp.vo.GeoVO

class GeoRepository(private val geoHelper: GeoHelper) {

    suspend fun getCity( geo: GeoVO ) = geoHelper.getCity(geo)

}
/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 27 April 2025
 */

package ru.urbanmedic.testapp.data.api

import ru.urbanmedic.testapp.vo.GeoVO

class GeoHelper (private val geoService:GeoService) {

    suspend fun getCity(geo: GeoVO) = geoService.getCity(geo)

}
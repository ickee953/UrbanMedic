/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 5 May 2025
 */

package ru.urbanmedic.testapp.repository

import ru.urbanmedic.testapp.data.api.GeoHelper
import ru.urbanmedic.testapp.vo.GeoVO

class GeoRepository(private val geoHelper: GeoHelper) {

    suspend fun getCity( geo: GeoVO ) = geoHelper.getCity(geo)

}
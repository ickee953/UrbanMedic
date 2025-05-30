/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 26 April 2025
 */

package ru.urbanmedic.testapp

data class UserItem(
    val id: Long?,
    val email: String?,
    val lastname: String?,
    val editable: Boolean = false
)